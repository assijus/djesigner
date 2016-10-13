package br.jus.trf2.dje.signer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.jus.trf2.assijus.system.api.IAssijusSystem.DocIdPdfGetRequest;
import br.jus.trf2.assijus.system.api.IAssijusSystem.DocIdPdfGetResponse;
import br.jus.trf2.assijus.system.api.IAssijusSystem.IDocIdPdfGet;

public class DocIdPdfGet implements IDocIdPdfGet {

	@Override
	public void run(DocIdPdfGetRequest req, DocIdPdfGetResponse resp)
			throws Exception {
		Id id = new Id(req.id);

		byte[] pdf = retrievePdf(id);
		resp.doc = pdf;
	}

	protected static byte[] retrievePdf(Id id) throws Exception, SQLException {
		// Chama a procedure que recupera os dados do PDF para viabilizar a
		// assinatura
		//
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = Utils.getConnection();
			pstmt = conn.prepareStatement(Utils.getSQL("pdfinfo"));
			pstmt.setLong(1, id.cadernoid);
			rset = pstmt.executeQuery();

			while (rset.next()) {
				String conteudo = rset.getString("conteudo");

				byte[] pdf = Utils.hexToBytes(conteudo);
				return pdf;
			}
		} finally {
			if (rset != null)
				rset.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		throw new Exception("Não foi possível descomprimir o PDF.");
	}

	@Override
	public String getContext() {
		return "visualizar documento";
	}

}
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

		PdfData pdfd = retrievePdf(id);
		resp.doc = pdfd.pdf;
		resp.secret = pdfd.secret;
	}

	protected static PdfData retrievePdf(Id id) throws Exception, SQLException {
		PdfData pdfd = new PdfData();
		
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

				pdfd.pdf = Utils.hexToBytes(conteudo);
				pdfd.secret = rset.getString("secret");
				return pdfd;
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

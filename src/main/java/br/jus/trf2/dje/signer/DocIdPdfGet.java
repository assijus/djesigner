package br.jus.trf2.dje.signer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.crivano.swaggerservlet.SwaggerServlet;

import br.jus.trf2.assijus.system.api.IAssijusSystem.DocIdPdfGetRequest;
import br.jus.trf2.assijus.system.api.IAssijusSystem.DocIdPdfGetResponse;
import br.jus.trf2.assijus.system.api.IAssijusSystem.IDocIdPdfGet;

public class DocIdPdfGet implements IDocIdPdfGet {

	@Override
	public void run(DocIdPdfGetRequest req, DocIdPdfGetResponse resp) throws Exception {
		Id id = new Id(req.id);

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
				SwaggerServlet.getHttpServletResponse().addHeader("Doc-Secret", rset.getString("secret"));
				resp.inputstream = rset.getBinaryStream("conteudo");
				SwaggerServlet.flush(req, resp);
				return;
			}
		} finally {
			if (rset != null)
				rset.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		throw new Exception("Não foi possível localizar o PDF.");
	}

	@Override
	public String getContext() {
		return "visualizar documento";
	}

}

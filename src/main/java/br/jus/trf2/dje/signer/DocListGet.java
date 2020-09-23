package br.jus.trf2.dje.signer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.crivano.swaggerservlet.SwaggerServlet;

import br.jus.trf2.assijus.system.api.IAssijusSystem.DocListGetRequest;
import br.jus.trf2.assijus.system.api.IAssijusSystem.DocListGetResponse;
import br.jus.trf2.assijus.system.api.IAssijusSystem.Document;
import br.jus.trf2.assijus.system.api.IAssijusSystem.IDocListGet;

public class DocListGet implements IDocListGet {

	@Override
	public void run(DocListGetRequest req, DocListGetResponse resp) throws Exception {

		// Parse request
		String cpf = req.cpf;

		// Setup json array
		List<Document> list = new ArrayList<>();

		String cpfs = DJESignerServlet.getProp("cpfs");
		if (cpfs.contains(cpf)) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rset = null;
			try {
				conn = Utils.getConnection();
				pstmt = conn.prepareStatement(Utils.getSQL("list"));
				// pstmt.setString(1, cpf);
				rset = pstmt.executeQuery();

				while (rset.next()) {
					Document doc = new Document();

					Id id = new Id(rset.getLong("id"));
					doc.id = id.toString();
					doc.secret = rset.getString("secret");
					doc.code = rset.getString("code");
					doc.descr = rset.getString("descr");
					doc.kind = rset.getString("kind");
					doc.origin = "DJe";
					list.add(doc);

					// Acrescenta essa informação na tabela para permitir a
					// posterior visualização.
					Utils.store(cpf + "-" + id.toString(), new byte[] { 1 });
				}
			} finally {
				if (rset != null)
					rset.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			}
		}
		resp.list = list;
	}

	@Override
	public String getContext() {
		return "listar documentos";
	}

}

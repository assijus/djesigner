package br.jus.trf2.dje.signer;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.jus.trf2.assijus.system.api.IAssijusSystem.DocIdHashGetRequest;
import br.jus.trf2.assijus.system.api.IAssijusSystem.DocIdHashGetResponse;
import br.jus.trf2.assijus.system.api.IAssijusSystem.IDocIdHashGet;

public class DocIdHashGet implements IDocIdHashGet {
	@Override
	public void run(DocIdHashGetRequest req, DocIdHashGetResponse resp) throws Exception {
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
				Hashes hashes = calcHashes(rset.getBinaryStream("conteudo"));

				// Produce response
				resp.sha1 = hashes.sha1;
				resp.sha256 = hashes.sha256;
				resp.secret = rset.getString("secret");
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

	public static class Hashes {
		byte[] sha1;
		byte[] sha256;
	}

	public static Hashes calcHashes(InputStream is) throws NoSuchAlgorithmException, IOException {
		Hashes hashes = new Hashes();

		MessageDigest mdSha1 = MessageDigest.getInstance("SHA-1");
		mdSha1.reset();

		MessageDigest mdSha256 = MessageDigest.getInstance("SHA-256");
		mdSha256.reset();

		byte[] buf = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			mdSha1.update(buf, 0, len);
			mdSha256.update(buf, 0, len);
		}
		is.close();

		hashes.sha1 = mdSha1.digest();
		hashes.sha256 = mdSha256.digest();
		return hashes;
	}

	@Override
	public String getContext() {
		return "obter o hash";
	}

}

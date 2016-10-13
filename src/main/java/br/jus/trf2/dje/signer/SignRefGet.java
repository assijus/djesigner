package br.jus.trf2.dje.signer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import br.jus.trf2.assijus.system.api.IAssijusSystem.ISignRefGet;
import br.jus.trf2.assijus.system.api.IAssijusSystem.SignRefGetRequest;
import br.jus.trf2.assijus.system.api.IAssijusSystem.SignRefGetResponse;

public class SignRefGet implements ISignRefGet {

	@Override
	public void run(SignRefGetRequest req, SignRefGetResponse resp)
			throws Exception {
		// Chama a procedure que faz a leitura da assinatura
		//
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			conn = Utils.getConnection();
			pstmt = conn.prepareStatement(Utils.getSQL("signinfo"));
			pstmt.setLong(1, Long.parseLong(req.ref));
			rset = pstmt.executeQuery();

			while (rset.next()) {
				String conteudo = rset.getString("envelope_assinatura");
				resp.envelope = Utils.hexToBytes(conteudo);
				resp.time = rset.getTimestamp("data_assinatura");
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

	@Override
	public String getContext() {
		return "salvar assinatura";
	}

}
package br.jus.trf2.dje.signer;

import java.sql.CallableStatement;
import java.sql.Connection;

import br.jus.trf2.assijus.system.api.IAssijusSystem.DocIdSignPutRequest;
import br.jus.trf2.assijus.system.api.IAssijusSystem.DocIdSignPutResponse;
import br.jus.trf2.assijus.system.api.IAssijusSystem.IDocIdSignPut;

import com.crivano.swaggerservlet.SwaggerCall;
import com.crivano.swaggerservlet.SwaggerUtils;

public class DocIdSignPut implements IDocIdSignPut {

	@Override
	public void run(DocIdSignPutRequest req, DocIdSignPutResponse resp)
			throws Exception {
		Id id = new Id(req.id);
		String detached = Utils.bytesToHex(req.envelope);

		// Chama a procedure que faz a gravação da assinatura
		//
		Connection conn = null;
		CallableStatement cstmt = null;
		try {
			conn = Utils.getConnection();

			cstmt = conn.prepareCall(Utils.getSQL("save"));

			cstmt.setString(1, detached);
			java.sql.Timestamp d = new java.sql.Timestamp(req.time.getTime());
			cstmt.setTimestamp(2, d);
			cstmt.setLong(3, id.cadernoid);
			cstmt.execute();
		} finally {
			if (cstmt != null)
				cstmt.close();
			if (conn != null)
				conn.close();
		}
		resp.status = "OK";
	}

	@Override
	public String getContext() {
		return "salvar assinatura";
	}

}
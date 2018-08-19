package com.avior.utils;

import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.avior.net.sms.NexmoClient;
import com.avior.net.sms.SMSProvider;

public class MySQLDBDAO implements TrackingDAO {

	private Logger logger = Logger.getLogger(getClass());

	private String user;
	private String pass;
	private String proceso;
	private String urlConexion;
	private String callProceso;

	@Override
	public int insertaRegistro(Long serial, double Lat, double Longitud,
			double Vel, double altitud, Date fecha, int noSatelite,
			String voltaje, int batConectada, String fechaString) {

		int rows = 0;
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(urlConexion, user,
					pass);
			logger.debug("Conectandose a " + urlConexion);
			CallableStatement st = conn.prepareCall(callProceso);
			st.setLong(1, serial);
			// Para formatear los numeros al estandar de la base de datos
			// DECIMAL(9,6)
			DecimalFormat decimal96 = new DecimalFormat("#########.######");
			decimal96.setRoundingMode(RoundingMode.HALF_EVEN);
			st.setDouble(4, Double.valueOf(decimal96.format(altitud)));
			st.setDouble(5, Vel);

			// Cambiar a String

			// Datos para el proceso de hiperion
			st.setDouble(2, Double.valueOf(decimal96.format(Longitud)));
			st.setDouble(3, Double.valueOf(decimal96.format(Lat)));
			st.setString(6, fechaString);
			st.setInt(7, noSatelite);
			st.setString(8, voltaje);
			st.setInt(9, batConectada);
			st.setString(10, "manuel");

			Integer sendSMS = 0;
			String email = null, celular = null, nombreCoche = null;

			if (st.execute()) {
				logger.debug("Ejecutado en base de datos.");
				// El Stored Procedure regresa las siguientes columnas:
				// -sendSMS (1,0) determina si se debe enviar o no un SMS
				// -email (String) Correo al cual enviar correo
				// -telefono (String) Numero de celular al cual se enviara un
				// SMS
				// -coche (String) Nombre del coche en cuestion
				ResultSet rs = st.getResultSet();
				if (rs.next()) {
					sendSMS = rs.getInt("sendSMS");
					email = rs.getString("email");
					celular = rs.getString("telefono");
					nombreCoche = rs.getString("coche");
				} else {
					logger.error("Error critico, el procedimiento almacenado no regreso ningun valor");
					// return;
				}
				rs.close();
			}

			if (sendSMS == 1 && (celular != null)) {
				logger.debug("El vehiculo ha salido de su cerca asignada, enviando SMS/email");
				logger.debug("Se enviará email a " + email);
				if (!celular.startsWith("521"))
					celular = "521" + celular;
				SMSProvider sms = NexmoClient.getInstance();
				sms.sendSMS(celular, nombreCoche); // El mensaje esta definido
													// en sms.properties y solo
													// modificamos el nombre del
													// coche
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			logger.error(e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
		return rows;
	}

	@Override
	public void configDAO(String user, String pass, String proceso,
			String urlConexion, String callProceso) {

		this.user = user;
		this.pass = pass;
		this.proceso = proceso;
		this.urlConexion = urlConexion;
		this.callProceso = callProceso;

	}

	@Override
	public EstadoEstacionamiento leeEstadoEstacionamiento(Long serial) {
		EstadoEstacionamiento result = new EstadoEstacionamiento();
		Connection conn = null;
		PreparedStatement status = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(urlConexion, user, pass);
			status = conn
					.prepareStatement("SELECT trackerBD.TAVcatDispositivos.fcNumeroSerie AS `serial`,"
							+ " trackerBD.TAVcatDispositivos.fiStatus AS `status`, "
							+ " trackerBD.TAVcatDispositivos.fiLatitud AS latitud,"
							+ " trackerBD.TAVcatDispositivos.fiLongitud AS longitud, "
							+ " trackerBD.TAVusuarioDisp.fiIdUsuario AS idUsuario,"
							+ " trackerBD.TAVdetalleUsuario.fcTelefono AS telefono "
							+ " FROM trackerBD.TAVcatDispositivos, "
							+ "trackerBD.TAVusuarioDisp, "
							+ "trackerBD.TAVdetalleUsuario"
							+ " WHERE trackerBD.TAVcatDispositivos.fcNumeroserie=? && "
							+ " trackerBD.TAVusuarioDisp.fcNumeroserie=trackerBD.TAVcatDispositivos.fcNumeroserie &&"
							+ " trackerBD.TAVdetalleUsuario.fiIdUsuario = trackerBD.TAVusuarioDisp.fiIdUsuario");
			logger.debug("Conexion y statement creado...");
			status.setLong(1, serial);
			ResultSet rs = status.executeQuery();
			logger.debug("Statement ejecutado, leyendo del serial " + serial);
			if (!rs.next()) {
				logger.error("No se encontraron registros de estacionamiento que coincidieran con la busqueda");
				return null;
			} else {
				//Se espera unicamente un resultado
				result.setLatitudFijada(rs.getDouble("latitud"));
				result.setLongitudFijada(rs.getDouble("longitud"));
				result.setSerial(rs.getLong("serial"));
				result.setStatus(rs.getInt("status"));
				result.setTelefonoContacto(rs.getString("telefono"));
				logger.debug(result);
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			logger.error(e.getCause());
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (status != null)
					status.close();
			} catch (SQLException e) {
			} // no se hace nada
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return result;
	}

	@Override
	public void actualizaEstadoEstacionamiento(Long serial, Integer status) {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(urlConexion, user, pass);
			st = conn
					.prepareStatement("UPDATE `trackerBD`.`TAVcatDispositivos` SET `fiStatus` = ? WHERE `fcNumeroserie` = ?");
			st.setInt(1, status);
			st.setLong(2, serial);
			Integer result = st.executeUpdate();
			logger.info("Resultado de update: " + result);

		} catch (SQLException e) {
			logger.error("Error al actualizar estado del dispositivo " + serial);
			logger.error(e.getMessage());
			logger.error(e.getCause());
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
			} // no se hace nada
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
	}

}

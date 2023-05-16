package br.com.restaurante.softexpert.domain;

import java.io.Serializable;

public class ClienteValorContaPorcentagem implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String cliente;
	private Double valorConta;
	private Double porcentagem;
	private String valorContaFinal;
	
	
	
	
	
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	public Double getValorConta() {
		return valorConta;
	}
	public void setValorConta(Double valorConta) {
		this.valorConta = valorConta;
	}
	public Double getPorcentagem() {
		return porcentagem;
	}
	public void setPorcentagem(Double porcentagem) {
		this.porcentagem = porcentagem;
	}
	public String getValorContaFinal() {
		return valorContaFinal;
	}
	public void setValorContaFinal(String valorContaFinal) {
		this.valorContaFinal = valorContaFinal;
	}
}
	
package br.com.restaurante.softexpert.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PedidoResponseCreatePedido implements Serializable {


	private static final long serialVersionUID = 1L;

	private Double valorTotal;
	
	private Long codigoPedido;
	
	private List<ClienteValorContaPorcentagem> listaClientesComValorConta = new ArrayList<>();

	
	
	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Long getCodigoPedido() {
		return codigoPedido;
	}

	public void setCodigoPedido(Long codigoPedido) {
		this.codigoPedido = codigoPedido;
	}

	public List<ClienteValorContaPorcentagem> getListaClientesComValorConta() {
		return listaClientesComValorConta;
	}

	public void setListaClientesComValorConta(List<ClienteValorContaPorcentagem> listaClientesComValorConta) {
		this.listaClientesComValorConta = listaClientesComValorConta;
	}
}

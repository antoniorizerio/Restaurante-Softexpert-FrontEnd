package br.com.restaurante.softexpert.domain;

import java.util.ArrayList;
import java.util.List;

public class PedidoDomain {

	private String moment;
	
	private Double desconto;
	
	private Double taxaEntrega;
	
	private Long codigoPedido;
	
	private Double valorTotalPedido;
	
	private Double taxaServico;
	
	private Boolean pedidoPresencial;
	
	private List<ItemPedidoDomain> itensPedidoDominio = new ArrayList<>();
	
	
	


	public String getMoment() {
		return moment;
	}

	public void setMoment(String moment) {
		this.moment = moment;
	}

	public Double getDesconto() {
		return desconto;
	}

	public void setDesconto(Double desconto) {
		this.desconto = desconto;
	}

	public Double getTaxaEntrega() {
		return taxaEntrega;
	}

	public void setTaxaEntrega(Double taxaEntrega) {
		this.taxaEntrega = taxaEntrega;
	}

	public Long getCodigoPedido() {
		return codigoPedido;
	}

	public void setCodigoPedido(Long codigoPedido) {
		this.codigoPedido = codigoPedido;
	}

	public Double getValorTotalPedido() {
		return valorTotalPedido;
	}

	public void setValorTotalPedido(Double valorTotalPedido) {
		this.valorTotalPedido = valorTotalPedido;
	}

	public List<ItemPedidoDomain> getItensPedidoDominio() {
		return itensPedidoDominio;
	}

	public void setItensPedidoDominio(List<ItemPedidoDomain> itensPedidoDominio) {
		this.itensPedidoDominio = itensPedidoDominio;
	}

	public Double getTaxaServico() {
		return taxaServico;
	}

	public void setTaxaServico(Double taxaServico) {
		this.taxaServico = taxaServico;
	}

	public Boolean getPedidoPresencial() {
		return pedidoPresencial;
	}

	public void setPedidoPresencial(Boolean pedidoPresencial) {
		this.pedidoPresencial = pedidoPresencial;
	}
	
}

package br.com.restaurante.softexpert.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class PedidoInsert {

	private Double desconto;
	
	private Double taxaEntrega;
	
	private Double taxaServico;
	
	private Boolean pedidoPresencial;
	
	private List<ItemPedidoDomain> itemsPedidoDominio;
	
	
	

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

	public List<ItemPedidoDomain> getItemsPedidoDominio() {
		if(Objects.isNull(itemsPedidoDominio)) {
			new HashSet<>();
		}
		return itemsPedidoDominio;
	}

	public void setItemsPedidoDominio(List<ItemPedidoDomain> itemsPedidoDominio) {
		this.itemsPedidoDominio = itemsPedidoDominio;
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

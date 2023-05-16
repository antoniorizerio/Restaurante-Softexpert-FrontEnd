package br.com.restaurante.softexpert.domain;

import java.io.Serializable;
import java.util.Objects;

public class ItemPedidoDomain implements Serializable {


	private static final long serialVersionUID = -1501073116096359871L;

	private ProdutoDomain produtoDominio;	
	
	private Integer quantity;
	
	private String cliente;
	
	public ItemPedidoDomain() {
		
	}
	
	public ItemPedidoDomain(ProdutoDomain produtoDominio, Integer quantity, String cliente) {
		super();
		this.produtoDominio = produtoDominio;
		this.quantity = quantity;
		this.cliente = cliente;
	}

	public ProdutoDomain getProdutoDominio() {
		if(Objects.isNull(produtoDominio)) {
			new ProdutoDomain();
		}
		return produtoDominio;
	}

	public void setProdutoDominio(ProdutoDomain produtoDominio) {
		this.produtoDominio = produtoDominio;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}
	
}

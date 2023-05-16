package br.com.restaurante.softexpert.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class ProdutoRequest implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private List<ProdutoDomain> listaProdutosDominio = new ArrayList<>();
	
	
	
	

	public List<ProdutoDomain> getListaProdutosDominio() {
		return listaProdutosDominio;
	}

	public void setListaProdutosDominio(List<ProdutoDomain> listaProdutosDominio) {
		this.listaProdutosDominio = listaProdutosDominio;
	}
	
}
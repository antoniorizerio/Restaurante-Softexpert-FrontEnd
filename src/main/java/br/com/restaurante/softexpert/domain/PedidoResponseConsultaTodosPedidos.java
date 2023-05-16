package br.com.restaurante.softexpert.domain;

import java.util.ArrayList;
import java.util.List;


public class PedidoResponseConsultaTodosPedidos {

	List<PedidoDomain> listPedidosDomain = new ArrayList<>();
	

	public List<PedidoDomain> getListPedidosDomain() {
		return listPedidosDomain;
	}

	public void setListPedidosDomain(List<PedidoDomain> listPedidosDomain) {
		this.listPedidosDomain = listPedidosDomain;
	}
	
}

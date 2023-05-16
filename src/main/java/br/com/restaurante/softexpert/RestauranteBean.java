package br.com.restaurante.softexpert;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.restaurante.softexpert.domain.ClienteValorContaPorcentagem;
import br.com.restaurante.softexpert.domain.ItemPedidoDomain;
import br.com.restaurante.softexpert.domain.PedidoDomain;
import br.com.restaurante.softexpert.domain.PedidoInsert;
import br.com.restaurante.softexpert.domain.PedidoResponseConsultaDeterminadoPedido;
import br.com.restaurante.softexpert.domain.PedidoResponseConsultaTodosPedidos;
import br.com.restaurante.softexpert.domain.PedidoResponseCreatePedido;
import br.com.restaurante.softexpert.domain.PedidoResponseExcluirDeterminadoPedido;
import br.com.restaurante.softexpert.domain.ProdutoDomain;
import br.com.restaurante.softexpert.domain.ProdutoRequest;
import br.com.restaurante.softexpert.exception.StandardError;

@ViewScoped
@ManagedBean(name = "restauranteBean")
public class RestauranteBean {

	
	private static String URL_PEDIDOS = "http://localhost:8090/pedidos";
	private static String URL_PRODUTOS = "http://localhost:8090/produtos";
	
	private PedidoInsert pedidoInsert = new PedidoInsert();
	
	private String taxaDesconto;
	private String taxaEntrega;
	private String taxaServico;
	private Integer quantidade;
	
	private Long codigoProduto;
	private Integer indexAba;
	private String cliente;
	
	private Boolean finalizarPedido = false;
	
	private String textoResultadoPedidos;
	private String textoTodosPedidosCadastrados;
	private String textoDeterminadoPedidoCadastrado;
	private String textoExcluirDeterminadoPedido;
	private String msgErro;
	
	private Integer numeroPedido;
	private Integer numeroPedidoExcluir;
	
	private Boolean pedidoPresencial;
	
	private List<ItemPedidoDomain> itemsPedidoDominio;
	
	private List<ProdutoDomain> listaProdutosDominio;
	
	private List<ProdutoDomain> listaProdutosCombo = new ArrayList<>();
	
	@PostConstruct
	public void init() {
		listaProdutosCombo = carregaProdutosRestaurante();
	}
	
	public List<ProdutoDomain> carregaProdutosRestaurante() {
		try {
			Response response = getInvocationBuilder(URL_PRODUTOS).get();
			String json = response.readEntity(String.class);
			ProdutoRequest produto = null;
			
	        ObjectMapper objectMapper = new ObjectMapper();
			produto = objectMapper.readValue(json, ProdutoRequest.class);
	        setListaProdutosDominio(produto.getListaProdutosDominio());
	        return produto.getListaProdutosDominio();
    
		} catch (ProcessingException e) {
			if(e.getMessage().contains("Connection refused")) {
				setMsgErro("Conexão Recusada com o Servidor Remoto!");
			}
			
			e.printStackTrace();
		} 
		catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}
	
	public void configNovoPedido() {
		setCliente(null);
		setQuantidade(null);
		setItemsPedidoDominio(new ArrayList<>());
		setTaxaDesconto(null);
		setTaxaEntrega(null);
		setTextoResultadoPedidos(null);
		setPedidoPresencial(Boolean.FALSE);
	}
	
	public void recuperarDeterminadoPedido() {
		try {
			setIndexAba(2);
			Client client = ClientBuilder.newClient();
			
			StringBuffer urlPedidos = new StringBuffer();
			urlPedidos.append(RestauranteBean.URL_PEDIDOS);
			urlPedidos.append("/");
			urlPedidos.append(getNumeroPedido());
			
			WebTarget webTarget = client.target(urlPedidos.toString());
			
			Invocation.Builder invocationBuilder =
						webTarget.request(MediaType.APPLICATION_JSON_TYPE);
			
			Response response = invocationBuilder.get();
			String json = response.readEntity(String.class);
			ObjectMapper objectMapper = new ObjectMapper();
			
			if(response.getStatus() == 404) {
				StandardError error = objectMapper.readValue(json, StandardError.class);
				setTextoDeterminadoPedidoCadastrado(error.getMessage());
				return;
				
			} else if(response.getStatus() == 200) {
				PedidoResponseConsultaDeterminadoPedido pedidoResponse = 
						objectMapper.readValue(json, PedidoResponseConsultaDeterminadoPedido.class);
	            configTextoDeterminadoPedidoCadastrado(pedidoResponse);
			}
		
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void excluirDeterminadoPedido() {
		try {
			setIndexAba(3);
			Client client = ClientBuilder.newClient();
			
			StringBuffer urlPedidos = new StringBuffer();
			urlPedidos.append(RestauranteBean.URL_PEDIDOS);
			urlPedidos.append("/");
			urlPedidos.append(getNumeroPedidoExcluir());
			
			WebTarget webTarget = client.target(urlPedidos.toString());
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
			
			Response response = invocationBuilder.delete();
			String json = response.readEntity(String.class);
			ObjectMapper objectMapper = new ObjectMapper();
			
			if(response.getStatus() == 404) {
				StandardError error = objectMapper.readValue(json, StandardError.class);
				setTextoExcluirDeterminadoPedido(error.getMessage());
				return;
				
			} else if(response.getStatus() == 200) {
				PedidoResponseExcluirDeterminadoPedido pedidoResponse = null;
	            pedidoResponse = objectMapper.readValue(json, PedidoResponseExcluirDeterminadoPedido.class);
	            configTextoExcluirDeterminadoPedido(pedidoResponse);
			}
		
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void recuperarTodosPedidos() {
		try {	
			setIndexAba(1);
			Response response = getInvocationBuilder(URL_PEDIDOS).get();
			if(response.getStatus() == 200) {
				String json = response.readEntity(String.class);
				PedidoResponseConsultaTodosPedidos pedidoResponse = null;
	            ObjectMapper objectMapper = new ObjectMapper();
	            pedidoResponse = objectMapper.readValue(json, PedidoResponseConsultaTodosPedidos.class);
	            configSaidaRecuperarTodosPedidos(pedidoResponse);
			} 
			

		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void adicionarPedido() {
		setIndexAba(0);
		if(!verificaCamposAdicionarPedido()) {
			return;
		}
		Optional<ProdutoDomain> optProdutoDominio = getListaProdutosDominio().stream().
				filter(produtoDominio -> produtoDominio.getId().intValue() == getCodigoProduto()).findFirst();
		
		if(optProdutoDominio.isPresent()) {
			ItemPedidoDomain itemPedidoDominio = new ItemPedidoDomain(optProdutoDominio.get(),getQuantidade(),
					getCliente());
			getItemsPedidoDominio().add(itemPedidoDominio);
		}
	}
	
	public void finalizarPedido() {
		try {
			setIndexAba(0);
			configObjetoPedido();
			
			Response response = getInvocationBuilder(URL_PEDIDOS).post(Entity.entity(getPedidoInsert(), MediaType.APPLICATION_JSON_TYPE));
			String json = response.readEntity(String.class);
			
			if(response.getStatus() == 201) {
				PedidoResponseCreatePedido pedidoResponse = new ObjectMapper().readValue(json, PedidoResponseCreatePedido.class);
	            configTextoResultadoPedidos(pedidoResponse);
			} else {
				StandardError pedidoResponse = new ObjectMapper().readValue(json, StandardError.class);
	            setMsgErro(pedidoResponse.getStatus() + " - " + pedidoResponse.getError() + " - " +
	            		pedidoResponse.getMessage());
			}
			
		} catch (Exception e) {
			if(e.getMessage().contains("Connection refused")) {
				setMsgErro("Conexão Recusada com o Servidor Remoto!");
			}
			e.printStackTrace();
		} 
	}
	
	
	
	
	
	
	
	
	// Métodos Auxiliares //
	
	private boolean verificaCamposAdicionarPedido() {
		StringBuffer msgErro = new StringBuffer();
		if(getQuantidade() == null) {
			msgErro.append("Campo Quantidade Obrigatório!");
			msgErro.append("\n");
		} if(getCliente() == null || getCliente().trim().length() == 0) {
			msgErro.append("Campo Cliente Obrigatório!\n");
		}
		if(msgErro.isEmpty()) {
			setMsgErro(null);
			return true;
		}
		setMsgErro(msgErro.toString());
		return false;
	}
	
	private void configObjetoPedido() {
		Double taxaDesconto = 0.0;
		if(!Objects.isNull(getTaxaDesconto()) && !getTaxaDesconto().isEmpty()) {
			taxaDesconto = Double.parseDouble(getTaxaDesconto().replace(",", "."));
		}
		Double taxaEntrega = 0.0;
		if(!Objects.isNull(getTaxaEntrega()) && !getTaxaEntrega().isEmpty()) {
			taxaEntrega = Double.parseDouble(getTaxaEntrega().replace(",", "."));
		}
		Double taxaServico = 0.0;
		if(!Objects.isNull(getTaxaServico()) && !getTaxaServico().isEmpty()) {
			taxaServico = Double.parseDouble(getTaxaServico().replace(",", "."));
		}
		
		getPedidoInsert().setDesconto(taxaDesconto);
		getPedidoInsert().setTaxaEntrega(taxaEntrega);
		getPedidoInsert().setTaxaServico(taxaServico);
		getPedidoInsert().setPedidoPresencial(getPedidoPresencial());
		getPedidoInsert().setItemsPedidoDominio(getItemsPedidoDominio());
	}
	
	private Invocation.Builder getInvocationBuilder(String url) {
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(url);
		Invocation.Builder invocationBuilder =
					webTarget.request(MediaType.APPLICATION_JSON_TYPE);
		return invocationBuilder;
	}
	
	private void configTextoResultadoPedidos(PedidoResponseCreatePedido pedidoResponse) {
		
		StringBuffer textoPedidosRealizados = new StringBuffer();
		textoPedidosRealizados.append(" ** -- ** ");
		textoPedidosRealizados.append("Código do Pedido: ");
		textoPedidosRealizados.append(pedidoResponse.getCodigoPedido());
		textoPedidosRealizados.append("\n");
		textoPedidosRealizados.append("Valor Total do Pedido: ");
		textoPedidosRealizados.append(pedidoResponse.getValorTotal());
		textoPedidosRealizados.append("\n");
		
		List<ClienteValorContaPorcentagem> listaClientesValorConta = pedidoResponse.getListaClientesComValorConta();
		
		for(ClienteValorContaPorcentagem clienteValor : listaClientesValorConta) {
			textoPedidosRealizados.append(" ** -- Cliente: ");
			textoPedidosRealizados.append(clienteValor.getCliente());
			textoPedidosRealizados.append(" /- Valor da Conta: ");
			textoPedidosRealizados.append(clienteValor.getValorContaFinal());
			textoPedidosRealizados.append("\n");
		}
		setTextoResultadoPedidos(textoPedidosRealizados.toString());
	}
	
	private void configSaidaRecuperarTodosPedidos(PedidoResponseConsultaTodosPedidos pedidoResponse) {
		
		List<PedidoDomain> listaPedidoDomain = pedidoResponse.getListPedidosDomain();
		
		StringBuffer textoPedidosRealizados = new StringBuffer();
		Instant instant = null;
		String data = null;
		List<ItemPedidoDomain> itensPedidoDominio = null;
		// horário de Brasília
		ZoneId timezone = ZoneId.of("America/Sao_Paulo");
		
		if(!Objects.isNull(listaPedidoDomain)) {
			
			if(listaPedidoDomain.isEmpty()) {
				setTextoTodosPedidosCadastrados("*** - Nenhum Pedido Cadastrado....");
				return;
			}
			
			for(PedidoDomain pedidoDominio : listaPedidoDomain) {
				textoPedidosRealizados.append("*****");
				textoPedidosRealizados.append("\n");
				textoPedidosRealizados.append("Pedido: ");
				textoPedidosRealizados.append(pedidoDominio.getCodigoPedido());
				textoPedidosRealizados.append(" - ");
				textoPedidosRealizados.append("Realizado Em: ");
				
				instant = Instant.parse(pedidoDominio.getMoment());
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
				data = formatter.format(instant.atZone(timezone));
				
				textoPedidosRealizados.append(data);
								
				textoPedidosRealizados.append("\n");
				
				if(pedidoDominio.getPedidoPresencial()) {
					textoPedidosRealizados.append("Taxa de Serviço: ");
					textoPedidosRealizados.append(pedidoDominio.getTaxaServico());
				} else {
					textoPedidosRealizados.append("Taxa de Entrega: ");
					textoPedidosRealizados.append(pedidoDominio.getTaxaEntrega());
					textoPedidosRealizados.append(" - ");
					textoPedidosRealizados.append("Taxa de Desconto: ");
					textoPedidosRealizados.append(pedidoDominio.getDesconto());
				}
				
				textoPedidosRealizados.append(" - ");
				textoPedidosRealizados.append("Valor Total: ");
				textoPedidosRealizados.append(pedidoDominio.getValorTotalPedido());
				
				itensPedidoDominio = pedidoDominio.getItensPedidoDominio();
				
				if(!Objects.isNull(itensPedidoDominio)) {
					textoPedidosRealizados.append("\n");
					textoPedidosRealizados.append("**Itens do Pedido: ");
					
					for(ItemPedidoDomain itemPedido : itensPedidoDominio) {
						
						textoPedidosRealizados.append("*****");
						textoPedidosRealizados.append("\n");
						textoPedidosRealizados.append("Produto: ");
						textoPedidosRealizados.append(itemPedido.getProdutoDominio().getName());
						textoPedidosRealizados.append(" - ");
						textoPedidosRealizados.append(itemPedido.getProdutoDominio().getPrice());
						textoPedidosRealizados.append("R$");
						textoPedidosRealizados.append(" - ");
						textoPedidosRealizados.append("Quantidade: ");
						textoPedidosRealizados.append(itemPedido.getQuantity());
						textoPedidosRealizados.append("\n");
						textoPedidosRealizados.append("Cliente: ");
						textoPedidosRealizados.append(itemPedido.getCliente());
						textoPedidosRealizados.append("\n");
					}
				}
			}
		}
	
		setTextoTodosPedidosCadastrados(textoPedidosRealizados.toString());
	}
	
	private void configTextoExcluirDeterminadoPedido(PedidoResponseExcluirDeterminadoPedido pedidoResponse) {
		PedidoDomain pedidoDominio = pedidoResponse.getPedidoDominio();
		StringBuffer textoPedidosRealizados = new StringBuffer();
		Instant instant = null;
		String data = null;
	 
		List<ItemPedidoDomain> itensPedidoDominio = null;
		// horário de Brasília
		ZoneId timezone = ZoneId.of("America/Sao_Paulo");
		
		if(!Objects.isNull(pedidoDominio)) {
			//for(PedidoDomain pedidoDominio : listaPedidoDomain) {
				textoPedidosRealizados.append("*****");
				textoPedidosRealizados.append("\n");
				textoPedidosRealizados.append("Pedido: ");
				textoPedidosRealizados.append(pedidoDominio.getCodigoPedido());
				textoPedidosRealizados.append(" - ");
				textoPedidosRealizados.append("Realizado Em: ");
				
				instant = Instant.parse(pedidoDominio.getMoment());
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
				data = formatter.format(instant.atZone(timezone));
				
				textoPedidosRealizados.append(data);
								
				textoPedidosRealizados.append("\n");
				
				if(pedidoDominio.getPedidoPresencial()) {
					textoPedidosRealizados.append("Taxa de Serviço: ");
					textoPedidosRealizados.append(pedidoDominio.getTaxaServico());
				} else {
					textoPedidosRealizados.append("Taxa de Entrega: ");
					textoPedidosRealizados.append(pedidoDominio.getTaxaEntrega());
					textoPedidosRealizados.append(" - ");
					textoPedidosRealizados.append("Taxa de Desconto: ");
					textoPedidosRealizados.append(pedidoDominio.getDesconto());
				}
				
				textoPedidosRealizados.append(" - ");
				textoPedidosRealizados.append("Valor Total: ");
				textoPedidosRealizados.append(pedidoDominio.getValorTotalPedido());
				
				itensPedidoDominio = pedidoDominio.getItensPedidoDominio();
				
				if(!Objects.isNull(itensPedidoDominio)) {
					textoPedidosRealizados.append("\n");
					textoPedidosRealizados.append("**Itens do Pedido: ");
					
					for(ItemPedidoDomain itemPedido : itensPedidoDominio) {
						
						textoPedidosRealizados.append("*****");
						textoPedidosRealizados.append("\n");
						textoPedidosRealizados.append("Produto: ");
						textoPedidosRealizados.append(itemPedido.getProdutoDominio().getName());
						textoPedidosRealizados.append(" - ");
						textoPedidosRealizados.append(itemPedido.getProdutoDominio().getPrice());
						textoPedidosRealizados.append("R$");
						textoPedidosRealizados.append(" - ");
						textoPedidosRealizados.append("Quantidade: ");
						textoPedidosRealizados.append(itemPedido.getQuantity());
						textoPedidosRealizados.append("\n");
						textoPedidosRealizados.append("Cliente: ");
						textoPedidosRealizados.append(itemPedido.getCliente());
						textoPedidosRealizados.append("\n");
					}
				}
			
		}
	
		setTextoExcluirDeterminadoPedido(textoPedidosRealizados.toString());
	}
	
	private void configTextoDeterminadoPedidoCadastrado(PedidoResponseConsultaDeterminadoPedido pedidoResponse) {
		PedidoDomain pedidoDominio = pedidoResponse.getPedidoDominio();
		StringBuffer textoPedidosRealizados = new StringBuffer();
		Instant instant = null;
		String data = null;
	 
		List<ItemPedidoDomain> itensPedidoDominio = null;
		// horário de Brasília
		ZoneId timezone = ZoneId.of("America/Sao_Paulo");
		
		if(!Objects.isNull(pedidoDominio)) {
			//for(PedidoDomain pedidoDominio : listaPedidoDomain) {
				textoPedidosRealizados.append("*****");
				textoPedidosRealizados.append("\n");
				textoPedidosRealizados.append("Pedido: ");
				textoPedidosRealizados.append(pedidoDominio.getCodigoPedido());
				textoPedidosRealizados.append(" - ");
				textoPedidosRealizados.append("Realizado Em: ");
				
				instant = Instant.parse(pedidoDominio.getMoment());
				
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
				data = formatter.format(instant.atZone(timezone));
				
				textoPedidosRealizados.append(data);
								
				textoPedidosRealizados.append("\n");
				
				if(pedidoDominio.getPedidoPresencial()) {
					textoPedidosRealizados.append("Taxa de Serviço: ");
					textoPedidosRealizados.append(pedidoDominio.getTaxaServico());
				} else {
					textoPedidosRealizados.append("Taxa de Entrega: ");
					textoPedidosRealizados.append(pedidoDominio.getTaxaEntrega());
					textoPedidosRealizados.append(" - ");
					textoPedidosRealizados.append("Taxa de Desconto: ");
					textoPedidosRealizados.append(pedidoDominio.getDesconto());
				}
				
				textoPedidosRealizados.append(" - ");
				textoPedidosRealizados.append("Valor Total: ");
				textoPedidosRealizados.append(pedidoDominio.getValorTotalPedido());
				
				itensPedidoDominio = pedidoDominio.getItensPedidoDominio();
				
				if(!Objects.isNull(itensPedidoDominio)) {
					textoPedidosRealizados.append("\n");
					textoPedidosRealizados.append("**Itens do Pedido: ");
					
					for(ItemPedidoDomain itemPedido : itensPedidoDominio) {
						
						textoPedidosRealizados.append("*****");
						textoPedidosRealizados.append("\n");
						textoPedidosRealizados.append("Produto: ");
						textoPedidosRealizados.append(itemPedido.getProdutoDominio().getName());
						textoPedidosRealizados.append(" - ");
						textoPedidosRealizados.append(itemPedido.getProdutoDominio().getPrice());
						textoPedidosRealizados.append("R$");
						textoPedidosRealizados.append(" - ");
						textoPedidosRealizados.append("Quantidade: ");
						textoPedidosRealizados.append(itemPedido.getQuantity());
						textoPedidosRealizados.append("\n");
						textoPedidosRealizados.append("Cliente: ");
						textoPedidosRealizados.append(itemPedido.getCliente());
						textoPedidosRealizados.append("\n");
					}
				}
		}
	
		setTextoDeterminadoPedidoCadastrado(textoPedidosRealizados.toString());
		
	}
	// Métodos Auxiliares //
	
	
	
	
	
	
	
	

	public Long getCodigoProduto() {
		return codigoProduto;
	}
	public void setCodigoProduto(Long codigoProduto) {
		this.codigoProduto = codigoProduto;
	}
	public String getCliente() {
		return cliente;
	}
	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public PedidoInsert getPedidoInsert() {
		return pedidoInsert;
	}

	public void setPedidoInsert(PedidoInsert pedidoInsert) {
		this.pedidoInsert = pedidoInsert;
	}

	public Boolean getFinalizarPedido() {
		return finalizarPedido;
	}

	public void setFinalizarPedido(Boolean finalizarPedido) {
		this.finalizarPedido = finalizarPedido;
	}

	public List<ProdutoDomain> getListaProdutosDominio() {
		return listaProdutosDominio;
	}

	public void setListaProdutosDominio(List<ProdutoDomain> listaProdutosDominio) {
		this.listaProdutosDominio = listaProdutosDominio;
	}

	public List<ItemPedidoDomain> getItemsPedidoDominio() {
		if(this.itemsPedidoDominio == null) {
			itemsPedidoDominio = new ArrayList<>();
		}
		return itemsPedidoDominio;
	}

	public void setItemsPedidoDominio(List<ItemPedidoDomain> itemsPedidoDominio) {
		this.itemsPedidoDominio = itemsPedidoDominio;
	}

	public String getTextoResultadoPedidos() {
		return textoResultadoPedidos;
	}

	public void setTextoResultadoPedidos(String textoResultadoPedidos) {
		this.textoResultadoPedidos = textoResultadoPedidos;
	}

	public String getTextoTodosPedidosCadastrados() {
		return textoTodosPedidosCadastrados;
	}

	public void setTextoTodosPedidosCadastrados(String textoTodosPedidosCadastrados) {
		this.textoTodosPedidosCadastrados = textoTodosPedidosCadastrados;
	}

	public Integer getIndexAba() {
		return indexAba;
	}

	public void setIndexAba(Integer indexAba) {
		this.indexAba = indexAba;
	}

	public String getTextoDeterminadoPedidoCadastrado() {
		return textoDeterminadoPedidoCadastrado;
	}

	public void setTextoDeterminadoPedidoCadastrado(String textoDeterminadoPedidoCadastrado) {
		this.textoDeterminadoPedidoCadastrado = textoDeterminadoPedidoCadastrado;
	}

	public Integer getNumeroPedido() {
		return numeroPedido;
	}

	public void setNumeroPedido(Integer numeroPedido) {
		this.numeroPedido = numeroPedido;
	}

	public String getTextoExcluirDeterminadoPedido() {
		return textoExcluirDeterminadoPedido;
	}

	public void setTextoExcluirDeterminadoPedido(String textoExcluirDeterminadoPedido) {
		this.textoExcluirDeterminadoPedido = textoExcluirDeterminadoPedido;
	}

	public Integer getNumeroPedidoExcluir() {
		return numeroPedidoExcluir;
	}

	public void setNumeroPedidoExcluir(Integer numeroPedidoExcluir) {
		this.numeroPedidoExcluir = numeroPedidoExcluir;
	}

	public Boolean getPedidoPresencial() {
		if(pedidoPresencial == null) {
			setPedidoPresencial(Boolean.FALSE);
		}
		return pedidoPresencial;
	}

	public void setPedidoPresencial(Boolean pedidoPresencial) {
		this.pedidoPresencial = pedidoPresencial;
	}

	public String getTaxaDesconto() {
		return taxaDesconto;
	}

	public void setTaxaDesconto(String taxaDesconto) {
		this.taxaDesconto = taxaDesconto;
	}

	public String getTaxaServico() {
		return taxaServico;
	}

	public void setTaxaServico(String taxaServico) {
		this.taxaServico = taxaServico;
	}

	public String getTaxaEntrega() {
		return taxaEntrega;
	}

	public void setTaxaEntrega(String taxaEntrega) {
		this.taxaEntrega = taxaEntrega;
	}

	public String getMsgErro() {
		return msgErro;
	}

	public void setMsgErro(String msgErro) {
		this.msgErro = msgErro;
	}

	public List<ProdutoDomain> getListaProdutosCombo() {
		return listaProdutosCombo;
	}

	public void setListaProdutosCombo(List<ProdutoDomain> listaProdutosCombo) {
		this.listaProdutosCombo = listaProdutosCombo;
	}
	
}

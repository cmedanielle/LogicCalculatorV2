import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExpTree {

	// raíz principal da árvore de expressão
	// neste caso, irá corresponder sempre a última operação a ser realizada
	private ExpTreeNode root;
	// nomes dasa proposições contidas na árvore (REFATORAR)
	private List<String> propositions;

	// ao criar o objeto, raíz setada para null
	// (isso só será modificado ao criarmos a árvore
	public ExpTree() {
		this.root = null;
		this.propositions = new ArrayList<>();
	}

	// método público, que irá interagir com a interface e outras classes
	public void buildTree(String formula) {
		this.root = this.buildTree(root, formula);
	}

	// métodos auxiliares devem ser privados (e aqui, temos sobrecarga para o
	// método recursivo)
	private ExpTreeNode buildTree(ExpTreeNode actualRoot, String subFormula) {

		// para verificar se há um determinado valor em uma string,
		// usamos o contains(substring), não é necessário percorrer toda a string com um
		// laço
		if (subFormula.contains("->")) {
			// retorna o índice da primeira ocorrência da
			// substring passada como parâmetro
			int index = subFormula.indexOf("->");

			// cria o nó com o valor da operação
			actualRoot = new ExpTreeNode("->");
			
			// chama método para inserir o filho à esquerda do nó atual
			// com a substring anterior à ocorrência do operador
			actualRoot.setLeftChild(buildTree(actualRoot.getLeftChild(), subFormula.substring(0, index)));
			// chama método para inserir o filho à direita do nó atual
			// com a substring subsequente à ocorrência do operador
			actualRoot.setRightChild(
					buildTree(actualRoot.getRightChild(), subFormula.substring(index+2, subFormula.length())));

			// como teremos que caminhar na árvore para realizar as inserções
			// precisamos retornar a referência aos nós intetrmediários percorridos
			return actualRoot;
		} // o mesmo processo se repete para os demais
		else if (subFormula.contains("+")) {
			int index = subFormula.indexOf("+");
			actualRoot = new ExpTreeNode("+");
			actualRoot.setLeftChild(buildTree(actualRoot.getLeftChild(), subFormula.substring(0, index)));
			actualRoot.setRightChild(
					buildTree(actualRoot.getRightChild(), subFormula.substring(++index, subFormula.length())));
			return actualRoot;
		} else if (subFormula.contains(".")) {
			int index = subFormula.indexOf(".");
			actualRoot = new ExpTreeNode(".");
			actualRoot.setLeftChild(buildTree(actualRoot.getLeftChild(), subFormula.substring(0, index)));
			actualRoot.setRightChild(
					buildTree(actualRoot.getRightChild(), subFormula.substring(++index, subFormula.length())));
			return actualRoot;
		} else if (subFormula.contains("~")) {
			int index = subFormula.indexOf("~");
			actualRoot = new ExpTreeNode("~");
			
			// no caso da negação, é necessário adicionar elementos apenas em um dos filhos
			// do nó atual
			actualRoot.setRightChild(
					buildTree(actualRoot.getRightChild(), subFormula.substring(++index, subFormula.length())));
			return actualRoot;
		}
		// se não encontrarmos nenhum dos operadores, é porque temos uma única
		// proposição na string
		this.propositions.add(subFormula);
		return new ExpTreeNode(subFormula);
	}
	
	// retorna o # total de proposições armazenadas na árvore
	public int getTotalPropostions() {
		return propositions.size();
	}
	
	// retorna as subfórmulas originadas da fórmula armazenada na árvore
	public String[] getSubFormulas() {
		List<String> result = new ArrayList<String>();
		getSubFormulas(this.root, result);
		String[] resultAsArray = new String[result.size()];
		return result.toArray(resultAsArray);
	}
	
	// verifica quais as subformulas recursivamente
	private List<String> getSubFormulas(ExpTreeNode actualRoot, List<String> subFormulas) {
		if (actualRoot != null) {
			if (actualRoot.toString().length() != 1 && !subFormulas.contains(actualRoot.toString())) {
					subFormulas.add(actualRoot.toString());
			}
			getSubFormulas(actualRoot.getLeftChild(), subFormulas);
			getSubFormulas(actualRoot.getRightChild(), subFormulas);
		}
		return subFormulas;
	}
	
	// retorna um array com as proposições organizadas em ordem alfabética
	public String[] getPropositions() {
		Collections.sort(propositions);
		return propositions.toArray(new String[propositions.size()]);
	}
	
	// avalia a árvore de expressão, dado um mapa com os valores de cada
	// proposição da fórmula
	public boolean evaluate(Map<String, Boolean> values) {
		return this.evaluate(values, this.root);
	}

	// método recursivo auxiliar para avaliação de cada subfórmula
	public boolean evaluate(Map<String, Boolean> values, ExpTreeNode actualRoot) {
		// verifica se o nó atual é uma folha; se for, verificamos qual o valor da proposição no mapa
		if (actualRoot.getLeftChild() == null && actualRoot.getRightChild() == null) {
			return Boolean.valueOf(values.get(actualRoot.getToken()));
		} // se não for, verificamos se a operação é uma negação
		else if (actualRoot.getToken().equals("~")) {
			// se for uma negação, avaliamos apenas seu filho da direita
			boolean value = evaluate(values, actualRoot.getRightChild());
			// retornamos o valor da operação para a subfórmula 
			return this.computeOperation(value, actualRoot.getToken());
		} // se não for, é porque se trata de um operador binário
		// então temos que avaliar a subfórmula que está a sua direita
		boolean leftValue = evaluate(values, actualRoot.getLeftChild());
		// e a subfórmula que está a sua esquerda
		boolean rightValue = evaluate(values, actualRoot.getRightChild());
		// para depois verificar qual o valora da operação em questão
		return this.computeOperation(leftValue, rightValue, actualRoot.getToken());
	}

	// computa o valor para a operação unária de negação
	private boolean computeOperation(boolean value, String token) {
		return !value;
	}

	// computa o valor para uma operação binária
	private boolean computeOperation(boolean leftValue, boolean rightValue, String token) {
		if (token.equals("->")) {
			return (leftValue && !rightValue ? false : true);
		} else if (token.equals("+")) {
			return (leftValue || rightValue);
		}
		return (leftValue && rightValue);
	}
	
	// métodos auxiliares, para verificar qual a fórmula armazenada na árvore de
	// expreessão
	public String infixTransversal() {
		return infixTransversal(this.root) + "\n";
	}

	// basicamente, aqui fazemos um in order tranversal na árvore
	private String infixTransversal(ExpTreeNode actualRoot) {
		if (actualRoot != null) {
			return infixTransversal(actualRoot.getLeftChild()) + actualRoot.getToken() + infixTransversal(actualRoot.getRightChild());
		}
		return "";
	}
	
	// retorna o nó da árvore
	public ExpTreeNode getRoot() {
		return this.root;
	}

}

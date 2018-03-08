import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExpTree {

	// ra�z principal da �rvore de express�o
	// neste caso, ir� corresponder sempre a �ltima opera��o a ser realizada
	private ExpTreeNode root;
	// nomes dasa proposi��es contidas na �rvore (REFATORAR)
	private List<String> propositions;

	// ao criar o objeto, ra�z setada para null
	// (isso s� ser� modificado ao criarmos a �rvore
	public ExpTree() {
		this.root = null;
		this.propositions = new ArrayList<>();
	}

	// m�todo p�blico, que ir� interagir com a interface e outras classes
	public void buildTree(String formula) {
		this.root = this.buildTree(root, formula);
	}

	// m�todos auxiliares devem ser privados (e aqui, temos sobrecarga para o
	// m�todo recursivo)
	private ExpTreeNode buildTree(ExpTreeNode actualRoot, String subFormula) {

		// para verificar se h� um determinado valor em uma string,
		// usamos o contains(substring), n�o � necess�rio percorrer toda a string com um
		// la�o
		if (subFormula.contains("->")) {
			// retorna o �ndice da primeira ocorr�ncia da
			// substring passada como par�metro
			int index = subFormula.indexOf("->");

			// cria o n� com o valor da opera��o
			actualRoot = new ExpTreeNode("->");
			
			// chama m�todo para inserir o filho � esquerda do n� atual
			// com a substring anterior � ocorr�ncia do operador
			actualRoot.setLeftChild(buildTree(actualRoot.getLeftChild(), subFormula.substring(0, index)));
			// chama m�todo para inserir o filho � direita do n� atual
			// com a substring subsequente � ocorr�ncia do operador
			actualRoot.setRightChild(
					buildTree(actualRoot.getRightChild(), subFormula.substring(index+2, subFormula.length())));

			// como teremos que caminhar na �rvore para realizar as inser��es
			// precisamos retornar a refer�ncia aos n�s intetrmedi�rios percorridos
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
			
			// no caso da nega��o, � necess�rio adicionar elementos apenas em um dos filhos
			// do n� atual
			actualRoot.setRightChild(
					buildTree(actualRoot.getRightChild(), subFormula.substring(++index, subFormula.length())));
			return actualRoot;
		}
		// se n�o encontrarmos nenhum dos operadores, � porque temos uma �nica
		// proposi��o na string
		this.propositions.add(subFormula);
		return new ExpTreeNode(subFormula);
	}
	
	// retorna o # total de proposi��es armazenadas na �rvore
	public int getTotalPropostions() {
		return propositions.size();
	}
	
	// retorna as subf�rmulas originadas da f�rmula armazenada na �rvore
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
	
	// retorna um array com as proposi��es organizadas em ordem alfab�tica
	public String[] getPropositions() {
		Collections.sort(propositions);
		return propositions.toArray(new String[propositions.size()]);
	}
	
	// avalia a �rvore de express�o, dado um mapa com os valores de cada
	// proposi��o da f�rmula
	public boolean evaluate(Map<String, Boolean> values) {
		return this.evaluate(values, this.root);
	}

	// m�todo recursivo auxiliar para avalia��o de cada subf�rmula
	public boolean evaluate(Map<String, Boolean> values, ExpTreeNode actualRoot) {
		// verifica se o n� atual � uma folha; se for, verificamos qual o valor da proposi��o no mapa
		if (actualRoot.getLeftChild() == null && actualRoot.getRightChild() == null) {
			return Boolean.valueOf(values.get(actualRoot.getToken()));
		} // se n�o for, verificamos se a opera��o � uma nega��o
		else if (actualRoot.getToken().equals("~")) {
			// se for uma nega��o, avaliamos apenas seu filho da direita
			boolean value = evaluate(values, actualRoot.getRightChild());
			// retornamos o valor da opera��o para a subf�rmula 
			return this.computeOperation(value, actualRoot.getToken());
		} // se n�o for, � porque se trata de um operador bin�rio
		// ent�o temos que avaliar a subf�rmula que est� a sua direita
		boolean leftValue = evaluate(values, actualRoot.getLeftChild());
		// e a subf�rmula que est� a sua esquerda
		boolean rightValue = evaluate(values, actualRoot.getRightChild());
		// para depois verificar qual o valora da opera��o em quest�o
		return this.computeOperation(leftValue, rightValue, actualRoot.getToken());
	}

	// computa o valor para a opera��o un�ria de nega��o
	private boolean computeOperation(boolean value, String token) {
		return !value;
	}

	// computa o valor para uma opera��o bin�ria
	private boolean computeOperation(boolean leftValue, boolean rightValue, String token) {
		if (token.equals("->")) {
			return (leftValue && !rightValue ? false : true);
		} else if (token.equals("+")) {
			return (leftValue || rightValue);
		}
		return (leftValue && rightValue);
	}
	
	// m�todos auxiliares, para verificar qual a f�rmula armazenada na �rvore de
	// expreess�o
	public String infixTransversal() {
		return infixTransversal(this.root) + "\n";
	}

	// basicamente, aqui fazemos um in order tranversal na �rvore
	private String infixTransversal(ExpTreeNode actualRoot) {
		if (actualRoot != null) {
			return infixTransversal(actualRoot.getLeftChild()) + actualRoot.getToken() + infixTransversal(actualRoot.getRightChild());
		}
		return "";
	}
	
	// retorna o n� da �rvore
	public ExpTreeNode getRoot() {
		return this.root;
	}

}

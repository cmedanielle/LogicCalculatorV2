
public class ExpTreeNode {

	// um nó da árvore de expressão contém um token
	// que pode ser uma proposição ou um operador lógico
	private String token;
	// um nó também pode possuir um valor lógico, ao ser avaliado
	private boolean value;

	private ExpTreeNode leftChild;
	private ExpTreeNode rightChild;

	public ExpTreeNode() {}
	
	// construtor + getters e setters
	public ExpTreeNode(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public ExpTreeNode getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(ExpTreeNode leftChild) {
		this.leftChild = leftChild;
	}

	public ExpTreeNode getRightChild() {
		return rightChild;
	}

	public void setRightChild(ExpTreeNode rightChild) {
		this.rightChild = rightChild;
	}

	// retorna a subfórmula pertencente ao operador do nó ou retorna uma proposição
	public String toString() {
		if (leftChild != null || rightChild != null) {
			if (token.equals("~")) {
				// se o nó contém um operador de negação, a subfórmula a qual ele pertence está a sua direita
				return token + rightChild.toString();
			}
			// se for outro operador, a subfórmula vai ser formada por um caminhamento infix na subárvore
			return leftChild.toString() + token + rightChild.toString();
		}
		// se for um nó folha, é apenas uma proposição :)
		return token;
	}
}

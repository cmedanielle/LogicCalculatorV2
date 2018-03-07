import java.util.Map;
import java.util.TreeMap;

public class TruthTable {

	// árvore da fórmula principal
	private ExpTree mainTree;
	// tabela-verdade
	private boolean[][] evaluation;
	// proposições contidas na tabela
	private String[] propositions;
	// subfórmulas (não são consideradas as proposições aqui)
	private String[] subFormulas;

	public TruthTable(String formula) {
		// constroi a árvore de expressão para toda a fórmula
		this.buildTree(formula);
		// verifica quais as proposições
		this.propositions = mainTree.getPropositions();
		// verifica quais as subfórmulas que deverão ser avaliadas
		this.subFormulas = mainTree.getSubFormulas();
	}

	// cria a árvore de expressão para a fórmula dada
	private void buildTree(String formula) {
		this.mainTree = new ExpTree();
		this.mainTree.buildTree(formula);
	}

	public boolean[][] createTable() {
		// inicializando a tabela com valores possíveis para cada proposição
		initializateTable();
		// as avaliaçães da fórmula devem ser armazenadas a partir da coluna
		// de índice igual ao total de proposições
		int initialColumn = propositions.length;
		return createTable(this.mainTree.getRoot(), 0, initialColumn);
	}

	private boolean[][] createTable(ExpTreeNode actualRoot, int actualRow, int actualColumn) {
		if (actualColumn < evaluation[0].length) {
			// se a raíz não estiver vazia, então temos um operador ou uma proposição
			if (actualRoot != null) {
				// verifica se estamos em um nó que contém um operador
				if (actualRoot.getLeftChild() != null || actualRoot.getRightChild() != null) {
					// enquanto o valor da linha não exceder o total de linhas da tabela-verdade
					// continuar avaliando com os valores possíveis das proposições
					if (actualRow < evaluation.length) {

						// cria um mapa para a relação entre proposição e valor lógico
						Map<String, Boolean> propsValue = new TreeMap<>();

						// armazena os valores previamente setados para cada proposição no mapa de
						// avaliação
						for (int i = 0; i < propositions.length; i++) {
							propsValue.put(propositions[i], evaluation[actualRow][i]);
						}

						// guarda o valor da avaliação da subfórmula na tabela-verdade
						evaluation[actualRow][actualColumn] = mainTree.evaluate(propsValue, actualRoot);
						// chama a próxima linha
						createTable(actualRoot, ++actualRow, actualColumn);
					}
				}
				// continua povoando a tabela-verdade para as demais subformulas
				createTable(actualRoot.getLeftChild(), 0, ++actualColumn);
				createTable(actualRoot.getRightChild(), 0, ++actualColumn);
			}
		}
		// tabela-verdade final
		return evaluation;
	}

	private void initializateTable() {
		// criando a tabela geral
		int totalPropositions = propositions.length;
		// # colunas = # de subformulas + # de proposições
		int columns = subFormulas.length + totalPropositions;
		// # linhas = 2^(# de proposições)
		int rows = (int) Math.pow(2, totalPropositions);
		evaluation = new boolean[rows][columns];

		// inserção dos valores possíveis para as proposiçoes
		for (int i = 0; i < rows; i++) {
			String bin = Integer.toBinaryString(i);
			// add 0s antes do valor, caso ele tenha o total
			// de dígitos igual ao total de colunas relacionadas a proposições atômicas
			while (bin.length() < totalPropositions) {
				bin = "0" + bin;
			}

			// cria um array auxiliar com total de colunas = total de proposições
			boolean[] boolArray = new boolean[totalPropositions];

			// converte o número binário, bit a bit, em valores booleanos
			int j = 0;
			while (j < totalPropositions) {
				// valor do bit atual
				char bit = bin.charAt(j);
				// verifica se o bit é 0, se for, valor é falso; true, caso contrário
				boolArray[j] = (Character.toString(bit).equals("0")) ? false : true;
				j++;
			}

			// inserção dos valores lógicos na tabela
			for (int k = 0; k < totalPropositions; k++) {
				evaluation[i][k] = boolArray[k];
			}
		}
	}
	
	public String[] getPropositions() {
		return this.propositions;
	}
	
	public String[] getSubFormulas() {
		return this.subFormulas;
	}
}

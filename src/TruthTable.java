import java.util.Map;
import java.util.TreeMap;

public class TruthTable {

	// �rvore da f�rmula principal
	private ExpTree mainTree;
	// tabela-verdade
	private boolean[][] evaluation;
	// proposi��es contidas na tabela
	private String[] propositions;
	// subf�rmulas (n�o s�o consideradas as proposi��es aqui)
	private String[] subFormulas;

	public TruthTable(String formula) {
		// constroi a �rvore de express�o para toda a f�rmula
		this.buildTree(formula);
		// verifica quais as proposi��es
		this.propositions = mainTree.getPropositions();
		// verifica quais as subf�rmulas que dever�o ser avaliadas
		this.subFormulas = mainTree.getSubFormulas();
	}

	// cria a �rvore de express�o para a f�rmula dada
	private void buildTree(String formula) {
		this.mainTree = new ExpTree();
		this.mainTree.buildTree(formula);
	}

	public boolean[][] createTable() {
		// inicializando a tabela com valores poss�veis para cada proposi��o
		initializateTable();
		// as avalia��es da f�rmula devem ser armazenadas a partir da coluna
		// de �ndice igual ao total de proposi��es
		int initialColumn = propositions.length;
		return createTable(this.mainTree.getRoot(), 0, initialColumn);
	}

	private boolean[][] createTable(ExpTreeNode actualRoot, int actualRow, int actualColumn) {
		if (actualColumn < evaluation[0].length) {
			// se a ra�z n�o estiver vazia, ent�o temos um operador ou uma proposi��o
			if (actualRoot != null) {
				// verifica se estamos em um n� que cont�m um operador
				if (actualRoot.getLeftChild() != null || actualRoot.getRightChild() != null) {
					// enquanto o valor da linha n�o exceder o total de linhas da tabela-verdade
					// continuar avaliando com os valores poss�veis das proposi��es
					if (actualRow < evaluation.length) {

						// cria um mapa para a rela��o entre proposi��o e valor l�gico
						Map<String, Boolean> propsValue = new TreeMap<>();

						// armazena os valores previamente setados para cada proposi��o no mapa de
						// avalia��o
						for (int i = 0; i < propositions.length; i++) {
							propsValue.put(propositions[i], evaluation[actualRow][i]);
						}

						// guarda o valor da avalia��o da subf�rmula na tabela-verdade
						evaluation[actualRow][actualColumn] = mainTree.evaluate(propsValue, actualRoot);
						// chama a pr�xima linha
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
		// # colunas = # de subformulas + # de proposi��es
		int columns = subFormulas.length + totalPropositions;
		// # linhas = 2^(# de proposi��es)
		int rows = (int) Math.pow(2, totalPropositions);
		evaluation = new boolean[rows][columns];

		// inser��o dos valores poss�veis para as proposi�oes
		for (int i = 0; i < rows; i++) {
			String bin = Integer.toBinaryString(i);
			// add 0s antes do valor, caso ele tenha o total
			// de d�gitos igual ao total de colunas relacionadas a proposi��es at�micas
			while (bin.length() < totalPropositions) {
				bin = "0" + bin;
			}

			// cria um array auxiliar com total de colunas = total de proposi��es
			boolean[] boolArray = new boolean[totalPropositions];

			// converte o n�mero bin�rio, bit a bit, em valores booleanos
			int j = 0;
			while (j < totalPropositions) {
				// valor do bit atual
				char bit = bin.charAt(j);
				// verifica se o bit � 0, se for, valor � falso; true, caso contr�rio
				boolArray[j] = (Character.toString(bit).equals("0")) ? false : true;
				j++;
			}

			// inser��o dos valores l�gicos na tabela
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

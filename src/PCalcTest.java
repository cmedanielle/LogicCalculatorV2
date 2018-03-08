import java.util.Arrays;

public class PCalcTest {
	
	public static void main(String[] args) {		
		TruthTable tt = new TruthTable("A.C+~B");
		System.out.println("** Tabela-verdade Resultante **\n");
		
		// retorna a tabela-verdade para a f�rmula
		boolean[][] table = tt.createTable();
		
		// visualiza��o da tabela
		String[] propositionsName = tt.getPropositions();
		String[] subFormulas = tt.getSubFormulas();
		
		// escrevendo as proposi��es e subf�rmulas
		for (int i = 0; i < propositionsName.length; i++) {
			System.out.print(propositionsName[i] + "\t");
		}
		for (int i = 0; i < subFormulas.length; i++) {
			System.out.print(subFormulas[i] + "\t");
		}
		
		// escrevendo os valores
		for (int i = 0; i < table.length; i++) {
			System.out.println();
			for (int j = 0; j < table[0].length; j++) {
				System.out.print(table[i][j] ? "T\t" : "F\t");
			}
		}		
	}

}

public class Main {
    public static void main(String[] args) {
        // Grau mínimo t = 3 -> Mínimo de 2 chaves e Máximo de 5 chaves por nó
        BTree bTree = new BTree(3);

        System.out.println("=== 1. INSERÇÕES INICIAIS (SEM SPLIT) ===");
        int[] iniciais = {80, 43, 33, 9, 60};
        for (int val : iniciais) {
            bTree.insert(val);
        }
        bTree.printStructure();

        System.out.println("\n=== 2. INSERINDO 15 (FORÇA O SPLIT DA RAIZ) ===");
        // Como a raiz já tem 5 chaves [9, 33, 43, 60, 80], inserir o 15 divide a raiz
        // e sobe a mediana (43) para uma nova raiz
        bTree.insert(15);
        bTree.printStructure();

        System.out.println("\n=== 3. INSERINDO MAIS ELEMENTOS PARA POPULAR OS FILHOS ===");
        int[] extras = {5, 20, 50, 70, 90, 100};
        for (int val : extras) {
            bTree.insert(val);
        }
        System.out.print("Percurso em ordem: ");
        bTree.printBTree();
        bTree.printStructure();

        System.out.println("\n=== 4. TESTE DE BUSCA (SEARCH) ===");
        System.out.println("Buscando 60: " + (bTree.search(60) != null ? "Encontrado!" : "Não encontrado"));
        System.out.println("Buscando 99: " + (bTree.search(99) != null ? "Encontrado!" : "Não encontrado"));

        System.out.println("\n=== 5. DELEÇÃO EM NÓ FOLHA COM FOLGA (Remover 100) ===");
        bTree.remove(100);
        bTree.printStructure();

        System.out.println("\n=== 6. DELEÇÃO COM EMPRÉSTIMO DE IRMÃO / BORROW (Remover 5) ===");
        // O filho esquerdo ficaria abaixo do mínimo (2 chaves), então pega emprestado do irmão
        bTree.remove(5);
        bTree.printStructure();

        System.out.println("\n=== 7. DELEÇÃO EM NÓ INTERNO (Remover 43 - Raiz) ===");
        // Substitui a raiz 43 pelo seu predecessor (33) ou sucessor (50)
        bTree.remove(43);
        bTree.printStructure();

        System.out.println("\n=== 8. DELEÇÕES COM FUSÃO / MERGE E REDUÇÃO DE ALTURA ===");
        bTree.remove(9);
        bTree.remove(15);
        bTree.remove(20);
        System.out.print("Percurso em ordem final: ");
        bTree.printBTree();
        bTree.printStructure();
    }
}
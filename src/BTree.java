public class BTree {
    private BTreeNode root;

    private int t;

    public BTree(int t) {
        this.t = t;
        root = null;
    }

    public BTreeNode search(int key) {
        return (root == null) ? null : root.search(key);
    }

    public void insert(int key) {
        if (root == null) {
            root = new BTreeNode(t, true);
            root.keys[0] = key;
            root.n = 1;
        } else {
            if (root.n == 2 * t - 1) {
                BTreeNode newRoot = new BTreeNode(t, false);
                newRoot.children[0] = root;
                newRoot.splitChild(0, root);

                int i = 0;

                if (newRoot.keys[0] < key)
                    i++;

                newRoot.children[i].insertNonFull(key);
                root = newRoot;
            } else {
                root.insertNonFull(key);
            }
        }
    }

    // Remove uma chave da Árvore B (pode reduzir a altura da árvore)
    public void remove(int key) {
        if (root == null) {
            System.out.println("A árvore está vazia.");
            return;
        }

        // Chama a remoção recursiva a partir da raiz
        root.remove(key);

        // Se a raiz ficou com 0 chaves após uma fusão (merge), ajustamos o ponteiro da raiz
        if (root.n == 0) {
            if (root.leaf) {
                root = null; // Árvore ficou totalmente vazia
            } else {
                root = root.children[0]; // O único filho fundido torna-se a nova raiz (altura diminui)
            }
        }
    }

    public void printStructure() {
        if (root == null) {
            System.out.println("  (Árvore vazia)");
        } else {
            root.printHierarchy(0);
        }
    }

    public void printBTree() {
        if (root != null)
            root.printInOrder();

        System.out.println();
    }
}
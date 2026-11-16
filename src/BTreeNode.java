public class BTreeNode {
        int[] keys;
        int t; //minimum
        BTreeNode[] children;
        int n; //number of keys
        boolean leaf;

        public BTreeNode(int t, boolean leaf) {
            this.t = t;
            this.leaf = leaf;

            keys = new int[2 * t - 1];
            children = new BTreeNode[2 * t];
        }

        public BTreeNode search(int key) {
            int i = 0;

            while(i < n && key > keys[i]) {
                i++;
            }

            if(i < n && keys[i] == key){
                return this;
            }

            if(leaf) {
                return null;
            }

            return children[i].search(key);
        }

    public void insertNonFull(int key) {
        int i = n - 1;

        if (leaf) {
            while (i >= 0 && key < keys[i]) {
                keys[i + 1] = keys[i];
                i--;
            }
            keys[i + 1] = key;
            n++;
        } else {
            while (i >= 0 && key < keys[i])
                i--;
            i++;

            if (children[i].n == 2 * t - 1) {
                splitChild(i, children[i]);
                if (key > keys[i])
                    i++;
            }
            children[i].insertNonFull(key);
        }
    }

    public void splitChild(int i, BTreeNode y) {
        BTreeNode z = new BTreeNode(y.t, y.leaf);
        z.n = t - 1;

        for (int j = 0; j < t - 1; j++)
            z.keys[j] = y.keys[j + t];

        if (!y.leaf) {
            for (int j = 0; j < t; j++)
                z.children[j] = y.children[j + t];
        }

        y.n = t - 1;

        for (int j = n; j >= i + 1; j--)
            children[j + 1] = children[j];

        children[i + 1] = z;

        for (int j = n - 1; j >= i; j--)
            keys[j + 1] = keys[j];

        keys[i] = y.keys[t - 1];
        n++;
    }

    // Retorna o índice da primeira chave maior ou igual a 'k' neste nó
    private int findKey(int k) {
        int idx = 0;
        while (idx < n && keys[idx] < k) {
            idx++;
        }
        return idx;
    }

    // Remove a chave 'k' da subárvore enraizada neste nó
    public void remove(int k) {
        int idx = findKey(k);

        // CASO A: A chave 'k' está presente neste nó (na posição idx)
        if (idx < n && keys[idx] == k) {
            if (leaf) {
                removeFromLeaf(idx);
            } else {
                removeFromNonLeaf(idx);
            }
        } else {
            // CASO B: A chave 'k' NÃO está neste nó
            if (leaf) {
                System.out.println("Aviso: A chave " + k + " não existe na árvore.");
                return;
            }

            // Indica se a chave está na subárvore do último filho (à extrema direita)
            boolean isLastChild = (idx == n);

            // Garante que o filho onde vamos descer tenha pelo menos 't' chaves
            if (children[idx].n < t) {
                fill(idx);
            }

            // Se o último filho foi fundido com o anterior, o índice do filho diminuiu em 1
            if (isLastChild && idx > n) {
                children[idx - 1].remove(k);
            } else {
                children[idx].remove(k);
            }
        }
    }

    // Caso 1: Remove a chave na posição 'idx' de um nó folha
    private void removeFromLeaf(int idx) {
        for (int i = idx + 1; i < n; i++) {
            keys[i - 1] = keys[i];
        }
        n--;
    }

    // Caso 2: Remove a chave na posição 'idx' de um nó interno
    private void removeFromNonLeaf(int idx) {
        int k = keys[idx];

        // Caso 2a: O filho à esquerda (children[idx]) tem pelo menos 't' chaves
        if (children[idx].n >= t) {
            int pred = getPredecessor(idx);
            keys[idx] = pred;
            children[idx].remove(pred);
        }
        // Caso 2b: O filho à direita (children[idx + 1]) tem pelo menos 't' chaves
        else if (children[idx + 1].n >= t) {
            int succ = getSuccessor(idx);
            keys[idx] = succ;
            children[idx + 1].remove(succ);
        }
        // Caso 2c: Ambos os filhos têm apenas (t - 1) chaves -> funde os dois filhos com 'k'
        else {
            merge(idx);
            children[idx].remove(k);
        }
    }

    // Busca o predecessor: maior chave da subárvore esquerda (extrema direita)
    private int getPredecessor(int idx) {
        BTreeNode cur = children[idx];
        while (!cur.leaf) {
            cur = cur.children[cur.n];
        }
        return cur.keys[cur.n - 1];
    }

    // Busca o sucessor: menor chave da subárvore direita (extrema esquerda)
    private int getSuccessor(int idx) {
        BTreeNode cur = children[idx + 1];
        while (!cur.leaf) {
            cur = cur.children[0];
        }
        return cur.keys[0];
    }

    // Preenche o filho children[idx] que está com (t - 1) chaves antes de descermos nele
    private void fill(int idx) {
        // Se o irmão esquerdo existir e tiver >= t chaves, pega emprestado dele
        if (idx != 0 && children[idx - 1].n >= t) {
            borrowFromPrev(idx);
        }
        // Se o irmão direito existir e tiver >= t chaves, pega emprestado dele
        else if (idx != n && children[idx + 1].n >= t) {
            borrowFromNext(idx);
        }
        // Caso nenhum irmão possa emprestar, faz merge com um irmão adjacente
        else {
            if (idx != n) {
                merge(idx);     // Funde children[idx] com children[idx + 1]
            } else {
                merge(idx - 1); // Se for o último filho, funde children[idx - 1] com children[idx]
            }
        }
    }

    // Caso 3a: Rotação à direita - pega a maior chave do irmão esquerdo via nó pai
    private void borrowFromPrev(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx - 1];

        // Desloca todas as chaves de 'child' para a direita para abrir espaço no índice 0
        for (int i = child.n - 1; i >= 0; i--) {
            child.keys[i + 1] = child.keys[i];
        }

        // Se não for folha, desloca também os ponteiros de filhos
        if (!child.leaf) {
            for (int i = child.n; i >= 0; i--) {
                child.children[i + 1] = child.children[i];
            }
        }

        // A chave separadora do pai desce para o início de 'child'
        child.keys[0] = keys[idx - 1];

        // O último filho do irmão esquerdo passa a ser o primeiro filho de 'child'
        if (!child.leaf) {
            child.children[0] = sibling.children[sibling.n];
            sibling.children[sibling.n] = null;
        }

        // A última chave do irmão esquerdo sobe para substituir a chave no pai
        keys[idx - 1] = sibling.keys[sibling.n - 1];

        child.n++;
        sibling.n--;
    }

    // Caso 3b: Rotação à esquerda - pega a menor chave do irmão direito via nó pai
    private void borrowFromNext(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];

        // A chave separadora do pai desce para o final de 'child'
        child.keys[child.n] = keys[idx];

        // O primeiro filho do irmão direito passa a ser o último filho de 'child'
        if (!child.leaf) {
            child.children[child.n + 1] = sibling.children[0];
        }

        // A primeira chave do irmão direito sobe para o pai
        keys[idx] = sibling.keys[0];

        // Desloca as chaves do irmão direito para a esquerda
        for (int i = 1; i < sibling.n; i++) {
            sibling.keys[i - 1] = sibling.keys[i];
        }

        // Desloca os filhos do irmão direito para a esquerda
        if (!sibling.leaf) {
            for (int i = 1; i <= sibling.n; i++) {
                sibling.children[i - 1] = sibling.children[i];
            }
            sibling.children[sibling.n] = null;
        }

        child.n++;
        sibling.n--;
    }

    // Caso 3c: Funde children[idx], a chave keys[idx] do pai e children[idx + 1] em um único nó
    private void merge(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];

        // Desce a chave keys[idx] do pai para o meio do nó 'child' (posição t - 1)
        child.keys[t - 1] = keys[idx];

        // Copia todas as chaves de 'sibling' para a segunda metade de 'child'
        for (int i = 0; i < sibling.n; i++) {
            child.keys[i + t] = sibling.keys[i];
        }

        // Se não for folha, copia também os ponteiros de filhos de 'sibling'
        if (!child.leaf) {
            for (int i = 0; i <= sibling.n; i++) {
                child.children[i + t] = sibling.children[i];
            }
        }

        // Fecha o buraco deixado por keys[idx] no nó pai
        for (int i = idx + 1; i < n; i++) {
            keys[i - 1] = keys[i];
        }

        // Fecha o buraco deixado pelo ponteiro de 'sibling' no nó pai
        for (int i = idx + 2; i <= n; i++) {
            children[i - 1] = children[i];
        }
        children[n] = null;

        // Atualiza contadores: 'child' agora tem (2t - 1) chaves e o pai perdeu 1 chave
        child.n += sibling.n + 1;
        n--;
    }

    public void printHierarchy(int level) {
        System.out.print("  ".repeat(level) + "Nível " + level + " [");
        for (int i = 0; i < n; i++) {
            System.out.print(keys[i] + (i < n - 1 ? ", " : ""));
        }
        System.out.println("]");

        if (!leaf) {
            for (int i = 0; i <= n; i++) {
                children[i].printHierarchy(level + 1);
            }
        }
    }


    public void printInOrder() {
        int i;

        for (i = 0; i < n; i++) {
            if (!leaf)
                children[i].printInOrder();
            System.out.print(keys[i] + " ");
        }

        if (!leaf)
            children[i].printInOrder();
    }
}
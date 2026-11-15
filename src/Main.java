public class Main {
    static void main() {
        BTree bTree = new BTree(3);
        bTree.insert(80);
        bTree.insert(43);
        bTree.insert(33);
        bTree.insert(9);
        bTree.insert(60);
        bTree.insert(15);

        System.out.print("B-tree : ");
        bTree.printBTree();

    }
}

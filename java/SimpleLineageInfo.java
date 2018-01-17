/**
 *
 * This class prints out the lineage info. It takes sql as input and prints
 * lineage info. Currently this prints only input and output tables for a given
 * sql. Later we can expand to add join tables etc.
 *
 */

// TODO add imports

public class SimpleLineageInfo implements NodeProcessor {

  /**
   * Stores input tables in sql.
   */
  TreeSet<String> inputTableList = new TreeSet<String>();
  /**
   * Stores output tables in sql.
   */
  TreeSet<String> OutputTableList = new TreeSet<String>();

  /**
   *
   * @return java.util.TreeSet
   */
  public TreeSet<String> getInputTableList() {
    return inputTableList;
  }

  /**
   * @return java.util.TreeSet
   */
  public TreeSet<String> getOutputTableList() {
    return OutputTableList;
  }

  /**
   * Implements the process method for the NodeProcessor interface.
   */
  public Object process(Node nd, Stack<Node> stack, NodeProcessorCtx procCtx,
      Object... nodeOutputs) throws SemanticException {
    ASTNode pt = (ASTNode) nd;

    switch (pt.getToken().getType()) {

    case HiveParser.TOK_TAB:
      OutputTableList.add(BaseSemanticAnalyzer.getUnescapedName((ASTNode)pt.getChild(0)));
      break;

    case HiveParser.TOK_TABREF:
      ASTNode tabTree = (ASTNode) pt.getChild(0);
      String table_name = (tabTree.getChildCount() == 1) ?
          BaseSemanticAnalyzer.getUnescapedName((ASTNode)tabTree.getChild(0)) :
            BaseSemanticAnalyzer.getUnescapedName((ASTNode)tabTree.getChild(0)) + "." + tabTree.getChild(1);
      inputTableList.add(table_name);
      break;
    }
    return null;
  }

  /**
   * parses given query and gets the lineage info.
   *
   * @param query
   * @throws ParseException
   */
  public void getLineageInfo(String query) throws ParseException,
      SemanticException {

    /*
     * Get the AST tree
     */
    ParseDriver pd = new ParseDriver();
    ASTNode tree = pd.parse(query);

    while ((tree.getToken() == null) && (tree.getChildCount() > 0)) {
      tree = (ASTNode) tree.getChild(0);
    }

    /*
     * initialize Event Processor and dispatcher.
     */
    inputTableList.clear();
    OutputTableList.clear();

    // create a walker which walks the tree in a DFS manner while maintaining
    // the operator stack. The dispatcher
    // generates the plan from the operator tree
    Map<Rule, NodeProcessor> rules = new LinkedHashMap<Rule, NodeProcessor>();

    // The dispatcher fires the processor corresponding to the closest matching
    // rule and passes the context along
    Dispatcher disp = new DefaultRuleDispatcher(this, rules, null);
    GraphWalker ogw = new DefaultGraphWalker(disp);

    // Create a list of topop nodes
    ArrayList<Node> topNodes = new ArrayList<Node>();
    topNodes.add(tree);
    ogw.startWalking(topNodes, null);
  }

  public static void main(String[] args) throws IOException, ParseException,
      SemanticException {

    String query = args[0];

    SimpleLineageInfo lep = new SimpleLineageInfo();

    lep.getLineageInfo(query);

    for (String tab : lep.getInputTableList()) {
      System.out.println("InputTable=" + tab);
    }

    for (String tab : lep.getOutputTableList()) {
      System.out.println("OutputTable=" + tab);
    }
  }
}

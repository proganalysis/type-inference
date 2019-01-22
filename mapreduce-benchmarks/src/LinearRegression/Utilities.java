package LinearRegression;

public class Utilities {

    public static String op_to_str(Operations op) {
        String str = op_to_str_internal(op);
        assert str != null;
        return str;
    }


    public static String comp_op_to_str(ComparisonOperator comp_op) {
        String str = comp_op_to_str_internal(comp_op);
        assert str != null;
        return str;
    }

    public static String msg_type_to_str(MessageType mtype) {
        String str = msg_type_to_str_internal(mtype);
        assert str != null;
        return str;
    }

    public static ComparisonOperator str_to_comp_op(String str) {
        ComparisonOperator comp_op = str_to_comp_op_internal(str);
        assert comp_op != null;
        return comp_op;
    }

    private static String op_to_str_internal(Operations op) {
        switch (op) {
            case MULTIPLY:
                return "MULTIPLY";
            case ROUND:
                return "ROUND";
            case DIVIDE:
                return "DIVIDE";
            case COMPARE:
                return "COMPARE";
        }
        return null;
    }

    private static String comp_op_to_str_internal(ComparisonOperator comp_op) {
        switch (comp_op) {
            case EQ:
                return "EQ";
            case GT:
                return "GT";
            case LT:
                return "LT";
            case NE:
                return "NE";
            case GTE:
                return "GTE";
            case LTE:
                return "LTE";
            default:
                return null;

        }
    }

    private static String msg_type_to_str_internal(MessageType mtype) {
        switch (mtype) {
            case OPE:
                return "OPE";
            case MSG:
                return "MSG";
            case VALUE:
                return "VALUE";
            case ROUND:
                return "ROUND";
            case OPD:
                return "OPD";
        }
        return null;
    }

    private static ComparisonOperator str_to_comp_op_internal(String comp_op) {
        switch (comp_op) {
            case "EQ":
                return ComparisonOperator.EQ;
            case "GT":
                return ComparisonOperator.GT;
            case "LT":
                return ComparisonOperator.LT;
            case "NE":
                return ComparisonOperator.NE;
            case "GTE":
                return ComparisonOperator.GTE;
            case "LTE":
                return ComparisonOperator.LTE;
            default:
                return null;
        }
    }

}

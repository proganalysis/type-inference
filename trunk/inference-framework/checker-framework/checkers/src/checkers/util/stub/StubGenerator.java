package checkers.util.stub;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.element.Modifier;

import checkers.util.TypesUtils;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.Context;

/**
 * Generates a stub file from a single class or an entire package.<p>
 *
 * A stub file can be used to add annotations to methods of classes, that
 * are only available in binary or the source of which cannot be edited.
 * For details, see the <a
 * href="http://types.cs.washington.edu/checker-framework/current/checkers-manual.html#stub-creating-and-using">Checker
 * Framework Manual</a>.
 */
public class StubGenerator {
    /** The used indention for the class */
    private final static String INDENTION = "    ";

    /** The output stream */
    private final PrintStream out;

    /** the current indention for the line being processsed */
    private String currentIndention = "";

    /** the package of the class being processed */
    // As an optimization, it is ended with a '.'
    private String currentPackage = null;

    private boolean needMethodBody = true;

    /**
     * Constructs an instanceof {@code IndexGenerator} that outputs to
     * {@code System.out}.
     */
    public StubGenerator() {
        this(System.out);
    }

    /**
     * Constructs an instance of {@code IndexGenerator} that outputs to
     * the provided output stream.
     *
     * @param out   the output stream
     */
    public StubGenerator(PrintStream out) {
        this.out = out;
    }

    /**
     * Generate the skeleton file for all the classes within the provided
     * package.
     */
    public void skeletonFromPackage(PackageElement packageElement) {
        currentPackage = packageElement.getQualifiedName().toString();

        indent();
        out.print("package ");
        out.print(currentPackage);
        out.println(";");
        currentPackage += ".";

        for (TypeElement element
                : ElementFilter.typesIn(packageElement.getEnclosedElements())) {
//            if (isPublicOrProtected(element)) {
            out.println();
            printClass((TypeElement)element);
//            }
        }
    }

    /**
     * Generate the skeleton file for provided class.  The generated file
     * includes the package name.
     */
    public void skeletonFromType(TypeElement typeElement) {

        // only output skeleton for classes or interfaces.  not enums
        if (typeElement.getKind() != ElementKind.CLASS
                && typeElement.getKind() != ElementKind.INTERFACE)
            return;

        String fullClassName = typeElement.getQualifiedName().toString();
        if (fullClassName.indexOf('.') != -1) {
            int index = fullClassName.lastIndexOf('.');
            currentPackage = fullClassName.substring(0, index);

            indent();

            out.print("package ");
            out.print(currentPackage);
            out.println(";");
            out.println();
            currentPackage += ".";
        }

        printClass(typeElement);
    }

    /**
     * helper method that outputs the index for the provided class.
     *
     * @param typeElement
     */
    private void printClass(TypeElement typeElement) {
        boolean prev = needMethodBody;
        indent();
        for (Modifier mo : typeElement.getModifiers()) {
            out.print(mo.toString() + " ");
        }
        boolean isEnum = false;
        if (typeElement.getKind() == ElementKind.INTERFACE) {
            out.print("interface");
            needMethodBody = false;
        } else if (typeElement.getKind() == ElementKind.CLASS) {
            out.print("class");
            needMethodBody = true;
        } else if (typeElement.getKind() == ElementKind.ENUM) {
            out.print("class");
            needMethodBody = true;
            isEnum = true;
        } else
            return;

        out.print(' ');
        out.print(typeElement.getSimpleName());

        // Type parameters
        if (!typeElement.getTypeParameters().isEmpty()) {
            out.print('<');
            out.print(formatList(typeElement.getTypeParameters()));
            out.print('>');
        }

        // Extends
        if (!isEnum && typeElement.getSuperclass().getKind() != TypeKind.NONE
                && !TypesUtils.isObject(typeElement.getSuperclass())) {
            out.print(" extends ");
            out.print(formatType(typeElement.getSuperclass()));
        }

        // implements
        if (!typeElement.getInterfaces().isEmpty()) {
            final boolean isInterface = typeElement.getKind() == ElementKind.INTERFACE;
            out.print(isInterface ? " extends " : " implements ");
            out.print(formatType(formatList(typeElement.getInterfaces())));
        }

        out.println(" {");
        String tempIndention = currentIndention;

        currentIndention = currentIndention + INDENTION;


        printTypeMembers(typeElement.getEnclosedElements());

        currentIndention = tempIndention;
        indent();
        out.println("}");
        needMethodBody = prev;
    }

    /**
     * Helper method that outputs the public or protected inner members of
     * a class.
     *
     * @param members list of the class members
     */
    private void printTypeMembers(List<? extends Element> members) {
        for (Element element : members) {
//            if (isPublicOrProtected(element))
            printMember(element);
        }
    }

    /**
     * Helper method that outputs the declaration of the member
     */
    private void printMember(Element member) {
        if (member.getKind().isField())
            printFieldDecl((VariableElement)member);
        else if (member instanceof ExecutableElement)
            printMethodDecl((ExecutableElement)member);
        else if (member instanceof TypeElement)
            printClass((TypeElement)member);
    }

    /**
     * Helper method that outputs the field declaration for the given field.
     *
     * It indicates whether the field is {@code protected}.
     */
    private void printFieldDecl(VariableElement field) {
        indent();

        boolean isFinal = false;

        for (Modifier mo : field.getModifiers()) {
            if (mo.toString().equals("final"))
                isFinal = true;
            out.print(mo.toString() + " ");
        }

        out.print(formatType(field.asType()));

        out.print(" ");
        out.print(field.getSimpleName());

        if (isFinal) {
            out.print(" = ");
            TypeKind kind = field.asType().getKind();
            switch (kind) {
                case BOOLEAN:
                    out.print("false");
                    break;
                case BYTE:
                case SHORT:
                case INT:
                case LONG:
                    out.print("0");
                    break;
                case CHAR:
                    out.print("\'c\'");
                    break;
                case FLOAT:
                case DOUBLE:
                    out.print("0.0");
                    break;
                default:
                    out.print("null");
            }
        }

        out.println(';');
    }

    /**
     * Helper method that outputs the method declaration for the given method
     *
     * IT indicates whether the field is {@code protected}.
     */
    private void printMethodDecl(ExecutableElement method) {
        if (method.toString().equals("<clinit>()"))
            return;
        indent();
        boolean prev = needMethodBody;

        for (Modifier mo : method.getModifiers()) {
            out.print(mo.toString() + " ");
            if (mo == Modifier.ABSTRACT)
                needMethodBody = false;
        }

        // print Generic arguments
        if (!method.getTypeParameters().isEmpty()) {
            out.print('<');
            out.print(formatList(method.getTypeParameters()));
            out.print("> ");
        }

        // not return type for constructors
        if (method.getKind() != ElementKind.CONSTRUCTOR) {
            out.print(formatType(method.getReturnType()));
            out.print(" ");
            out.print(method.getSimpleName());
        } else
            out.print(method.getEnclosingElement().getSimpleName());

        out.print('(');

        boolean isFirst = true;
        for (VariableElement param : method.getParameters()) {
            if (!isFirst) out.print(", ");
            out.print(formatType(param.asType()));
            out.print(' ');
            out.print(param.getSimpleName());
            isFirst = false;
        }

        out.print(')');

        if (!method.getThrownTypes().isEmpty()) {
            out.print(" throws ");
            out.print(formatType(method.getThrownTypes()));
        }
        if (!needMethodBody)
            out.println(';');
        else {
            // Add skeleton 
            out.println(" { throw new RuntimeException(\"skeleton method\"); }");
        }
        needMethodBody = prev;
    }

    /** Indent the current line */
    private void indent() {
        out.print(currentIndention);
    }

    /**
     * Return a string representation of the list in the form of
     * <code>
     *    item1, item2, item3, ...
     * </code>
     *
     * instead of the default representation,
     * <code>
     *    [item1, item2, item3, ...]
     * </code>
     *
     */
    private String formatList(List<?> lst) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Object o : lst) {
            if (!isFirst) sb.append(", ");
            sb.append(o);
            isFirst = false;
        }
        return sb.toString();
    }

    /** Returns true if the element is public or protected element */
    private boolean isPublicOrProtected(Element element) {
        return element.getModifiers().contains(Modifier.PUBLIC)
               || element.getModifiers().contains(Modifier.PROTECTED);
    }

    /** outputs the simple name of the type */
    private String formatType(Object typeRep) {
//        StringTokenizer tokenizer = new StringTokenizer(typeRep.toString(), "()<>[], ", true);
//        StringBuilder sb = new StringBuilder();

//        while (tokenizer.hasMoreTokens()) {
//            String token = tokenizer.nextToken();
//            if (token.length() == 1
//                    || token.lastIndexOf('.') == -1)
//                sb.append(token);
//            else {
//                int index = token.lastIndexOf('.');
//                sb.append(token.substring(index + 1));
//            }
//        }
//        return sb.toString();
        return typeRep.toString().replace('$', '.');
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage:");
            System.out.println("    java IndexGenerator [class or package name]");
            return;
        }

        Context context = new Context();
        ProcessingEnvironment env =
            new JavacProcessingEnvironment(context, Collections.<Processor>emptyList());

        StubGenerator generator = new StubGenerator();

        if (env.getElementUtils().getPackageElement(args[0]) != null)
            generator.skeletonFromPackage(env.getElementUtils().getPackageElement(args[0]));
        else if (env.getElementUtils().getTypeElement(args[0]) != null)
            generator.skeletonFromType(env.getElementUtils().getTypeElement(args[0]));
        else
            System.err.println("Couldn't find a package or a class named " + args[0]);
    }
}

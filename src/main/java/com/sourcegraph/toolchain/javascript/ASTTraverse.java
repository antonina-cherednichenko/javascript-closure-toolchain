package com.sourcegraph.toolchain.javascript;

import com.google.javascript.jscomp.*;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.rhino.Node;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ASTTraverse {
    private Compiler compiler;
    private CompilerOptions options;
    private SymbolTable table;

    public ASTTraverse(List<String> inputFilePaths, List<String> externFilePaths) {
        //initialize compiler and compiler options
        compiler = new Compiler();
        options = new CompilerOptions();
        options.setCheckTypes(true);
        //options.setInferTypes(true);
        options.setNewTypeInference(true);
        initCompilerOptions();

        CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(
                options);
        WarningLevel.VERBOSE.setOptionsForWarningLevel(options);

        initSymbolTable(inputFilePaths, externFilePaths);
    }

    private void initCompilerOptions() {
        options.setCodingConvention(new ClosureCodingConvention());
        options.setLanguage(CompilerOptions.LanguageMode.ECMASCRIPT5);


        //CommonJs modules work
        options.setProcessCommonJSModules(true);

        //AMD modules
        options.setTransformAMDToCJSModules(true);

        options.setIdeMode(true);

    }

    private void initSymbolTable(List<String> inputFilePaths, List<String> externFilePaths) {
        List<SourceFile> inputs = new ArrayList<SourceFile>();
        List<SourceFile> externs = new ArrayList<SourceFile>();

        //fill input files list
        for (String filePath : inputFilePaths) {
            File file = new File(filePath);
            try {
                inputs.add(SourceFile.fromCode(file.getName(), FileUtils.readFileToString(file)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //fill externs files list
        for (String filePath : externFilePaths) {
            File file = new File(filePath);
            try {
                externs.add(SourceFile.fromCode(file.getAbsolutePath(), FileUtils.readFileToString(file)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //compiler.parse();
        compiler.compile(externs, inputs, options);
        table = compiler.buildKnownSymbolTable();
    }

    public void traverse() {
        NodeTraversal.Callback cb = new NodeTraversal.AbstractPostOrderCallback() {

            public void visit(NodeTraversal t, Node node, Node parent) {


                if (node.isName() && !node.isFromExterns() && !parent.getParent().getSourceFileName().equals("{SyntheticVarsDeclar}")) {

                    if (parent.isVar()) {
                        SymbolTable.Symbol symbol = table.getEnclosingScope(node).getQualifiedSlot(node.getQualifiedName());
                        if (symbol != null) {
                            System.out.println("VAR for = " + symbol.getName() + ", type = " + symbol.getType());
                            System.out.println(node.getLineno() + ", " + node.getCharno());
                            System.out.println("DECL = " + symbol.getDeclarationNode().getQualifiedName() + " = " + symbol.getDeclarationNode().getLineno() + ", " + symbol.getDeclarationNode().getCharno()
                                    + ", " + symbol.getDeclarationNode().getSourceFileName());
                        } else {
                            System.out.println("Null symbol for node = " + node);
                        }

                    } else if (parent.isFunction()) {

                        if (node.getQualifiedName() != null) {
                            SymbolTable.Symbol symbol = table.getEnclosingScope(node).getQualifiedSlot(node.getQualifiedName());
                            if (symbol != null) {
                                System.out.println("FN for = " + symbol.getName() + ", type = " + symbol.getType());
                                System.out.println(node.getLineno() + ", " + node.getCharno());
                                System.out.println("DECL = " + symbol.getDeclarationNode().getQualifiedName() + " = " + symbol.getDeclarationNode().getLineno() + ", " + symbol.getDeclarationNode().getCharno()
                                        + ", " + symbol.getDeclarationNode().getSourceFileName());
                            } else {
                                System.out.println("Null symbol for node = " + node);
                            }
                        }

                    } else {

                        SymbolTable.Symbol symbol = table.getEnclosingScope(node).getQualifiedSlot(node.getQualifiedName());
                        if (symbol != null) {
                            System.out.println("USAGE for = " + symbol.getName() + ", type = " + symbol.getType());
                            System.out.println(node.getLineno() + ", " + node.getCharno() + "," + node.getSourceFileName());
                            System.out.println("DECL = " + symbol.getDeclarationNode().getQualifiedName() + " = " + symbol.getDeclarationNode().getLineno() + ", " + symbol.getDeclarationNode().getCharno()
                                    + ", " + symbol.getDeclarationNode().getSourceFileName());
                        } else {
                            System.out.println("Null symbol for node = " + node);
                        }
                    }
                }
                //System.out.println(node);
                //System.out.println(node.getQualifiedName());
                //System.out.println(node.toStringTree());
            }
        };

        Node top = compiler.getRoot();

        System.out.println(top.toStringTree());
        NodeTraversal.traverseEs6(compiler, top, cb);
        System.out.println(compiler.toSource());

    }

}

package com.sourcegraph.toolchain.javascript;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Run {

    //first arg - one js file for analysis
    //second arg - is directory with externs file
    public static void main(String[] args) {

        List<String> inputFilePaths = new ArrayList<String>();
        List<String> externFilePaths = new ArrayList<String>();

        File input = new File(args[0]);
        if (input.isDirectory()) {
            Collection<File> inputFiles = FileUtils.listFiles(input,
                    new RegexFileFilter("^(.*js)"),
                    DirectoryFileFilter.DIRECTORY
            );

            for (File inputFile : inputFiles) {
                inputFilePaths.add(inputFile.getAbsolutePath());
            }
        } else {
            inputFilePaths.add(input.getAbsolutePath());
        }


        File extern = new File(args[1]);
        if (extern.isDirectory()) {
            Collection<File> externFiles = FileUtils.listFiles(extern,
                    new RegexFileFilter("^(.*js)"),
                    DirectoryFileFilter.DIRECTORY
            );

            for (File externFile : externFiles) {
                externFilePaths.add(externFile.getAbsolutePath());
            }
        } else {
            externFilePaths.add(extern.getAbsolutePath());
        }

//        File extern2 = new File("/Users/tonya/closure-compiler/contrib/nodejs");
//        if (extern2.isDirectory()) {
//            Collection<File> externFiles = FileUtils.listFiles(extern2,
//                    new RegexFileFilter("^(.*js)"),
//                    DirectoryFileFilter.DIRECTORY
//            );
//
//            for (File externFile : externFiles) {
//                inputFilePaths.add(externFile.getAbsolutePath());
//                externFilePaths.add(externFile.getAbsolutePath());
//            }
//        }


        //inputFilePaths.add("/Users/tonya/javascript-tests/javascript-nodejs-sample-0/node_modules/should/lib/should.js");
        //inputFilePaths.add("/Users/tonya/javascript-tests/javascript-nodejs-sample-0/node_modules/should/lib/eql.js");

        ASTTraverse tree = new ASTTraverse(inputFilePaths, externFilePaths);
        tree.traverse();

    }
}

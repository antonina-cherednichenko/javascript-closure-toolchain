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

        inputFilePaths.add(args[0]);


        Collection<File> externFiles = FileUtils.listFiles(new File(args[1]),
                new RegexFileFilter("^(.*js)"),
                DirectoryFileFilter.DIRECTORY
        );

        for (File externFile : externFiles) {
            externFilePaths.add(externFile.getAbsolutePath());
            System.out.println(externFile.getPath());

        }


        ASTTraverse tree = new ASTTraverse(inputFilePaths, externFilePaths);
        tree.traverse();

    }
}

package com.mehalter.life.persistence;

import javax.swing.filechooser.FileFilter;
import java.io.File;

// file filter class to filter out non-lexicon files in the file choosers
// used in the program
public class CellFileFilter extends FileFilter {

    // defines the files allowed
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().endsWith(".cells");
    }

    // define the description to be shows in the file chooser
    @Override
    public String getDescription() {
        return "Lexicon files (*.cells)";
    }
}

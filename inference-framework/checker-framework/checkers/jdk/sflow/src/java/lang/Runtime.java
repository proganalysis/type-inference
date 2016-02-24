package java.lang;

import java.io.*;
import checkers.inference.sflow.quals.*;

class Runtime {
    Runtime getRuntime() {throw new RuntimeException("skeleton method");}
    void exit(int arg0) {throw new RuntimeException("skeleton method");}
    void addShutdownHook(Thread arg0) {throw new RuntimeException("skeleton method");}
    boolean removeShutdownHook(Thread arg0) {throw new RuntimeException("skeleton method");}
    void halt(int arg0) {throw new RuntimeException("skeleton method");}
    void runFinalizersOnExit(boolean arg0) {throw new RuntimeException("skeleton method");}
    Process exec(/*@Safe*/ String arg0) throws IOException {throw new RuntimeException("skeleton method");}
    Process exec(/*@Safe*/ String arg0, String[] arg1) throws IOException {throw new RuntimeException("skeleton method");}
    Process exec(/*@Safe*/ String arg0, String[] arg1, File arg2) throws IOException {throw new RuntimeException("skeleton method");}
    Process exec(/*@Safe*/ String/*@Safe*/ [] arg0) throws IOException {throw new RuntimeException("skeleton method");}
    Process exec(/*@Safe*/ String/*@Safe*/ [] arg0, String[] arg1) throws IOException {throw new RuntimeException("skeleton method");}
    Process exec(/*@Safe*/ String/*@Safe*/ [] arg0, String[] arg1, File arg2) throws IOException {throw new RuntimeException("skeleton method");}
    int availableProcessors() {throw new RuntimeException("skeleton method");}
    long freeMemory() {throw new RuntimeException("skeleton method");}
    long totalMemory() {throw new RuntimeException("skeleton method");}
    long maxMemory() {throw new RuntimeException("skeleton method");}
    void gc() {throw new RuntimeException("skeleton method");}
    void runFinalization() {throw new RuntimeException("skeleton method");}
    void traceInstructions(boolean arg0) {throw new RuntimeException("skeleton method");}
    void traceMethodCalls(boolean arg0) {throw new RuntimeException("skeleton method");}
    void load(/*@Safe*/ String arg0) {throw new RuntimeException("skeleton method");}
    void loadLibrary(/*@Safe*/ String arg0) {throw new RuntimeException("skeleton method");}
    InputStream getLocalizedInputStream(InputStream arg0) {throw new RuntimeException("skeleton method");}
    OutputStream getLocalizedOutputStream(OutputStream arg0) {throw new RuntimeException("skeleton method");}
}

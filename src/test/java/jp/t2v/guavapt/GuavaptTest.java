package jp.t2v.guavapt;

import java.nio.charset.Charset;
import java.util.Locale;

import jp.t2v.guavapt.Processor;

import org.seasar.aptina.unit.AptinaTestCase;

public class GuavaptTest extends AptinaTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addSourcePath("src/test/java");
        setLocale(Locale.JAPANESE);
        setCharset(Charset.forName("UTF-8"));
    }

    public void test() throws Exception {
        addProcessor(new Processor());

        addCompilationUnit(TestEntity.class);
        
        compile();

        assertEqualsGeneratedSourceWithResource("jp/t2v/guavapt/TestEntityFunctions.result", "jp.t2v.guavapt.TestEntityFunctions");
        assertEqualsGeneratedSourceWithResource("jp/t2v/guavapt/TestEntityPredicates.result", "jp.t2v.guavapt.TestEntityPredicates");
        
    }
    
    public void testSuffixOption() throws Exception {
        addProcessor(new Processor());

        addCompilationUnit(TestEntity.class);
        addOption("-Aguavapt.f.suffix=Hoge", "-Aguavapt.p.suffix=Mage");
        
        compile();

        assertEqualsGeneratedSourceWithResource("jp/t2v/guavapt/TestEntityHoge.result", "jp.t2v.guavapt.TestEntityHoge");
        assertEqualsGeneratedSourceWithResource("jp/t2v/guavapt/TestEntityMage.result", "jp.t2v.guavapt.TestEntityMage");
        
    }
    
}

package ch.uzh.ifi.ddis.mymedialite.main;

import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

public class TestItemRecommenderEvaluate extends TestCase {
	
	public void testGetRecommenderProperties() throws IOException {
		Properties p = ItemRecommenderEvaluate.getRecommenderProperties("   rec1bla3--_xy {x =   z   ,    dsfa123   : ijiswq912_12390  }      \t    ");
		
		assertEquals("z", p.get("x"));
		assertEquals("ijiswq912_12390", p.get("dsfa123"));
		
		p = ItemRecommenderEvaluate.getRecommenderProperties("   rec1bla3--_xy {  }      \t    ");	
		assertEquals(0, p.size());

		p = ItemRecommenderEvaluate.getRecommenderProperties("   rec1bla3--_xy      \t    ");	
		assertEquals(0, p.size());
	}
	
	public void testGetRecommenderName() throws IOException {
		String n = ItemRecommenderEvaluate.getRecommenderName("   rec1bla3--_xy {x =   z   ,    dsfa123   : ijiswq912_12390  }      \t    ");
		
		System.out.println(n);
		
		assertEquals("rec1bla3--_xy", n);

	}
	
	public void testConvertPropertyValueToType() throws IOException {
				
		assertEquals(-1.42342375, ItemRecommenderEvaluate.convertPropertyValueToType("-1.42342375"));
		assertEquals(-1034234, ItemRecommenderEvaluate.convertPropertyValueToType("-1034234"));

		assertEquals(true, ItemRecommenderEvaluate.convertPropertyValueToType("true"));
		assertEquals(false, ItemRecommenderEvaluate.convertPropertyValueToType("FALSE"));

		assertEquals("bla", ItemRecommenderEvaluate.convertPropertyValueToType("bla"));

		
	}

}

package net.openhft.chronicle.examples;

import net.openhft.chronicle.ChronicleConfig;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.ExcerptTailer;
import net.openhft.chronicle.IndexedChronicle;
import org.joda.time.DateTime;


public class ChronicleErrorCase {
	
	public static class TickDataTest {
		
		public final DateTime timeStamp;
		public final double price;
		public final double volume;
		public final int priceType;

		public TickDataTest(DateTime timeStamp, double price, double volume, int priceType){ 
			this.timeStamp = timeStamp; this.price = price; this.volume = volume; this.priceType = priceType; 
		}
	}

public static void write(TickDataTest tickData, ExcerptAppender toAppender){ 	
	toAppender.startExcerpt(); 
	toAppender.writeLong (tickData.timeStamp.getMillis()); 
	toAppender.writeDouble(tickData.price ); 
	toAppender.writeDouble(tickData.volume ); 
	toAppender.writeInt (tickData.priceType ); 
	toAppender.finish(); 
}


public static TickDataTest readTickData(ExcerptTailer tailer){ 
	long timeStamp = tailer.readLong (); 
	double price = tailer.readDouble(); 
	double volume = tailer.readDouble(); 
	int type = tailer.readInt (); 
	tailer.finish(); 
	return new TickDataTest(new DateTime(timeStamp), price, volume, type); 
}

public static void main(String[] args) {
	try {
		String dir = "C:\\Downloads\\Temp\\";//System.getProperty("java.io.tmpdir");		
		String basePath = dir + "test123";
		ChronicleConfig chronicleConfig = ChronicleConfig.DEFAULT;
		IndexedChronicle writeChronicle = new IndexedChronicle(basePath, chronicleConfig);
		ExcerptAppender appender = writeChronicle.createAppender();
		int numberOfTicks = 100000000;
		long startDate = 1423432423;
		for(int i=0; i<numberOfTicks; i++){ 
			TickDataTest td = new TickDataTest(new DateTime(startDate + i*10), Math.random(), Math.random(), 1); 
			write(td, appender); 
		}
		writeChronicle.close();
		IndexedChronicle readChronicle = new IndexedChronicle(basePath, chronicleConfig); ;
		ExcerptTailer tailer = readChronicle.createTailer();
		tailer.index(numberOfTicks * 2 / 3);
		int ticksRead = 0;
		while (tailer.nextIndex()){ 
			TickDataTest td = readTickData(tailer); ticksRead++; 
		}
		System.out.println("Read "+ticksRead+" ticks");
	} catch (Exception e){
		e.printStackTrace(); 
	}
}

}

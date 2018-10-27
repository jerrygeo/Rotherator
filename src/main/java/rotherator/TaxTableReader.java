package rotherator;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;

import java.io.Reader;
import java.util.Iterator;

public class TaxTableReader implements Iterable<TaxTable> {

    private CSVReader csvReader;

    public TaxTableReader(Reader reader) {
        csvReader = new CSVReader(reader);
    }

    public Iterator<TaxTable> iterator() {
        return new TaxTableIterator(csvReader.iterator());
    }

    private class TaxTableIterator implements Iterator<TaxTable> {
        private Iterator<String[]> baseIterator;

        public TaxTableIterator(Iterator<String[]> stringArrayIterator) {
            baseIterator = stringArrayIterator;
            baseIterator.next();
        }

        public boolean hasNext() {
            return baseIterator.hasNext();
        }

        public TaxTable next() {
            String[] tableHeaderLine = baseIterator.next();
            String [] incomeLine = baseIterator.next();
            String [] baseTaxLine = baseIterator.next();
            String [] marginalRateLine = baseIterator.next();
            String [] capitalGainsLine = baseIterator.next();

            TaxTable result = new TaxTable(
                    tableHeaderLine[0],
                    Float.parseFloat(tableHeaderLine[2]),
                    Float.parseFloat(tableHeaderLine[4]),
                    Float.parseFloat(tableHeaderLine[6]));

            int nBrackets = incomeLine.length - 1;

            for (int i=0; i<nBrackets; i++) {
                if (!StringUtils.isBlank(incomeLine[i+1])) {
                    float income = Float.parseFloat(incomeLine[i + 1]);
                    float base = Float.parseFloat(baseTaxLine[i + 1]);
                    float rate = Float.parseFloat(marginalRateLine[i + 1]);
                    float capGains = Float.parseFloat(capitalGainsLine[i + 1]);
                    TaxBracket nextBracket = new TaxBracket(income, base, rate, capGains);
                    result.brackets.add(nextBracket);
                }
            }

            return result;
        }
    }
}

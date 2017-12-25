import java.util.LinkedList;
/*
comments
 */

public class SpamFilter {
    private LinkedList<String> spamList;
    private BloomFilter<String> bloomFilter;    //collection
    private DBSpam dbSpam;
    private double falsePositiveProbability = 0.1;
    private int expectedNumberOfElements = 5;

    private long spamListSize = 0;

    public SpamFilter(double falsePositiveProbability, int expectedNumberOfElements, DBSpam dbSpam)
    {
        this.falsePositiveProbability = falsePositiveProbability;
        this.expectedNumberOfElements = expectedNumberOfElements;
        this.dbSpam = dbSpam;
        bloomFilter = new BloomFilter<String>(falsePositiveProbability, expectedNumberOfElements);
        spamList = new LinkedList<>();
        fillSpamList();
    }

    private void fillSpamList() {
        LinkedList<String> spamList = dbSpam.getAllSpam();
        for (String cur : spamList) {
            bloomFilter.add(cur);
            System.out.println("Added to BLOOM:" + cur + ":");
        }
     }
    private void clearSpamList() {
        spamList.clear();
    }

    public void reFillSpamList()
    {
        clearSpamList();
        bloomFilter = new BloomFilter<String>(falsePositiveProbability, expectedNumberOfElements);
        fillSpamList();
    }

    public boolean checkSpam(String text)
    {
        return bloomFilter.contains(text.toLowerCase());
    }

    public boolean checkSpam(String[] lines)
    {
        for (String cur : lines) {
            if(bloomFilter.contains(cur.toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }
}

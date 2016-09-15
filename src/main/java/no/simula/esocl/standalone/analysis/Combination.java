package no.simula.esocl.standalone.analysis;

import java.util.ArrayList;
import java.util.BitSet;

public class Combination {

    private ArrayList<String> combList = new ArrayList<String>();

    public static void main(String[] args) throws Exception {
        Combination comb = new Combination();
        comb.mn(new String[]{"1", "2", "3", "4", "5", "6"}, 3);
        for (int i = 0; i < comb.getCombList().size(); i++) {
            System.out.println(comb.getCombList().get(i));
            String[] list = comb.getCombList().get(i).split(",");
            Arrange ts = new Arrange();
            ts.perm(list, 0, list.length - 1);
            for (int j = 0; j < ts.getArrangeList().size(); j++) {
                System.out.println("/u0009" + ts.getArrangeList().get(j));
            }
        }
    }

    public void mn(String[] array, int n) {
        int m = array.length;
        if (m < n)
            throw new IllegalArgumentException("Error   m   <   n");
        BitSet bs = new BitSet(m);
        for (int i = 0; i < n; i++) {
            bs.set(i, true);
        }
        do {
            printAll(array, bs);
        } while (moveNext(bs, m));

    }

    /**
     * 1��start ��һ��trueƬ�ε���ʼλ��end��ֹλ
     * 2���ѵ�һ��trueƬ�ζ���false
     * 3�������0�±���ʼ���Ե�һ��trueƬ��Ԫ��������һΪ�±��λ�ö���true
     * 4���ѵ�һ��trueƬ��end��ֹλ��true
     *
     * @param bs �����Ƿ���ʾ�ı�־λ
     * @param m  ���鳤��
     * @return boolean �Ƿ����������
     */
    private boolean moveNext(BitSet bs, int m) {
        int start = -1;
        while (start < m)
            if (bs.get(++start))
                break;
        if (start >= m)
            return false;

        int end = start;
        while (end < m)
            if (!bs.get(++end))
                break;
        if (end >= m)
            return false;
        for (int i = start; i < end; i++)
            bs.set(i, false);
        for (int i = 0; i < end - start - 1; i++)
            bs.set(i);
        bs.set(end);
        return true;
    }

    /**
     * ������ɵ���Ͻ��
     *
     * @param array ����
     * @param bs    ����Ԫ���Ƿ���ʾ�ı�־λ����
     */
    private void printAll(String[] array, BitSet bs) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++)
            if (bs.get(i)) {
                sb.append(array[i]).append(',');
            }
        sb.setLength(sb.length() - 1);
        combList.add(sb.toString());
    }

    public ArrayList<String> getCombList() {
        return combList;
    }

}
import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.Font;
import java.applet.AudioClip;

public class SortingVisualization extends Applet implements Runnable, MouseListener, MouseMotionListener, KeyListener {
    int[] bricks = new int[193];
    int highest = 0;
    int spot = 0;
    int sortSpeed = 10;
    boolean bubble = false;
    boolean selection = false;
    boolean insertion = false;
    boolean radixSort = false;
    boolean heap = false;
    double b, s, i, r, h;
    String running = "";

    public void init() {
        setSize(1366, 675);
        setBackground(new Color(33, 33, 33));
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        createBricks();
    }

    public void createBricks() {

        for (int k = 0; k < bricks.length; k++) {
            bricks[k] = (int) (Math.random() * 450 + 1);
        }
        findHighest();
    }

    public void findHighest() {
        highest = 0;
        for (int i = 1; i < bricks.length; i++) {
            if (bricks[i] > highest) {
                highest = bricks[i];
                spot = i;
            }

        }
    }

    public void paint(Graphics g) {
        int x = 5;
        g.setColor(Color.WHITE);
        for (int i = 0; i < bricks.length; i++) {
            if (i == spot) {
                g.setColor(new Color(231, 76, 60));
                g.drawRect(x, 630 - bricks[i], 5, bricks[i]);
                g.setColor(Color.WHITE);
            } else {
                g.setColor(new Color(191, 191, 191));
                g.drawRect(x, 630 - bricks[i], 5, bricks[i]);
                g.setColor(Color.WHITE);
            }
            x += 7;
        }
        g.setColor(new Color(188, 211, 128));
        g.setFont(new Font("Times New Roman", Font.BOLD, 23));
        g.drawString("Click anywhere to randomize the histogram.", 800, 100);
        g.setColor(new Color(188, 211, 128));
        g.setFont(new Font("Times New Roman", Font.BOLD, 16));
        g.drawString("Sorts:", 10, 25);
        g.setColor(new Color(87, 122, 156));
        g.setFont(new Font("Times New Roman", Font.BOLD, 16));
        g.drawString("Press (s) for Selection Sort.   ->  " + s + " s.", 10, 45);
        g.drawString("Press (b) for Bubble Sort.   ->  " + b + " s.", 10, 65);
        g.drawString("Press (i) for Insertion Sort.   ->  " + i + " s.", 10, 85);
        g.drawString("Press (r) for Radix Sort.   ->  " + r + " s.", 10, 105);
        g.drawString("Press (h) for Heap Sort.   ->  " + h + " s.", 10, 125);
        g.drawString("Press (e) to exit.", 10, 145);
        g.setColor(new Color(188, 211, 128));
        g.drawString("Speeds:", 400, 25);
        g.setColor(new Color(87, 122, 156));
        g.drawString("Press (3) : Slow", 400, 45);
        g.drawString("Press (2) : Normal", 400, 65);
        g.drawString("Press (1) : Fast", 400, 85);
        g.drawString("Press (0) : Very Fast", 400, 105);
        g.setColor(new Color(156, 121, 87));
        g.setFont(new Font("Consolas", Font.BOLD, 16));
        g.drawString("> " + running, 10, 165);
    }

    public void bubble() {
        b = 0;
        long start = System.currentTimeMillis();
        for (int i = 0; i < bricks.length; i++) {
            for (int j = 1; j < bricks.length - i; j++) {

                if (bricks[j - 1] > bricks[j]) {
                    int temp1 = bricks[j - 1];
                    bricks[j - 1] = bricks[j];
                    bricks[j] = temp1;
                    findHighest();
                    repaint();
                    try {
                        Thread.sleep((long) (sortSpeed));
                    } catch (InterruptedException Ex) {
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        b = (end - start) * 0.001;
        bubble = false;
    }

    public void selection() {
        s = 0;
        long start = System.currentTimeMillis();
        for (int m = bricks.length - 1; m >= 1; m--) {
            int highest = bricks[0];
            int spot = 0;
            for (int i = 1; i <= m; i++) {
                if (bricks[i] > highest) {
                    highest = bricks[i];
                    spot = i;
                    findHighest();
                    repaint();
                    try {
                        Thread.sleep((long) (sortSpeed));
                    } catch (InterruptedException Ex) {
                    }
                }
            }
            int temp = bricks[m];
            bricks[m] = highest;
            bricks[spot] = temp;
        }
        long end = System.currentTimeMillis();
        s = (end - start) * 0.001;
        selection = false;
    }

    public void insertion() {
        i = 0;
        long start = System.currentTimeMillis();
        int temp = 0;
        for (int i = 1; i < bricks.length; i++) {

            temp = bricks[i];
            for (int j = i - 1; j >= 0 && bricks[j] > temp; j--) {
                bricks[j + 1] = bricks[j];
                bricks[j] = temp;
                findHighest();
                repaint();
                try {
                    Thread.sleep((long) (sortSpeed));
                } catch (InterruptedException Ex) {
                }
            }
        }
        long end = System.currentTimeMillis();
        i = (end - start) * 0.001;
        insertion = false;
    }

    public void radix() {
        r = 0;
        long start = System.currentTimeMillis();
        int infinity = 99999;
        int max = bricks.length;
        int[][] radix = new int[max][max];
        int[] bucketCounter = new int[max];
        for (int i = 0; i < max; i++) {
            bucketCounter[i] = 0;
            for (int j = 0; j < max; j++) {
                radix[i][j] = infinity;
            }
        }
        // find the length of the largest element
        int maxNum = 0;
        for (int i = 0; i < max; i++) {
            if (maxNum < bricks[i]) {
                maxNum = bricks[i];
            }
        }
        // find the length of the maxNum
        int length = 0;
        while (maxNum != 0) {
            length++;
            maxNum = maxNum / 10;
        }
        int placementIndex = 0;
        int placementCounter = 1;
        int arrayValue;
        while (length != 0) {
            // find the last, second to last, so on and so forth.
            for (int i = 0; i < max; i++) {
                arrayValue = bricks[i];
                for (int j = placementCounter; j != 0; j--) {
                    placementIndex = arrayValue % 10;// if 36, then this equals 6 (remainder)
                    arrayValue = arrayValue / 10;// if 36, then 3
                }
                radix[placementIndex][bucketCounter[placementIndex]] = bricks[i];
                bucketCounter[placementIndex]++;// increment the value
            }
            // fill the bucket with the values
            // put values back into sorted array
            // repeat
            // put into 2D array
            int horIndex = 0;
            int verIndex = 0;
            for (int i = 0; i < max; i++) {
                while (radix[verIndex][horIndex] == infinity) {
                    verIndex++;// next place in the bucket
                    horIndex = 0;
                }
                bricks[i] = radix[verIndex][horIndex];

                findHighest();
                repaint();
                try {
                    Thread.sleep((long) (sortSpeed));
                } catch (InterruptedException Ex) {
                }
                horIndex++;// next spot
            }
            // RESET FOR THE NEXT ROUND!!!
            for (int i = 0; i < max; i++) {
                bucketCounter[i] = 0;
                for (int j = 0; j < max; j++) {
                    radix[i][j] = infinity;
                }
            }
            placementCounter++; // --to the second place
            length--;

        }
        long end = System.currentTimeMillis();
        r = (end - start) * 0.001;
        radixSort = false;
    }

    public void heap() {
        h = 0;
        long start = System.currentTimeMillis();
        heapSort(bricks);
        long end = System.currentTimeMillis();
        h = (end - start) * 0.001;
        heap = false;
    }

    public void heapSort(int data[]) {
        int size = data.length;

        for (int i = size / 2 - 1; i >= 0; i--) {
            heapify(i, data, size);
        }

        for (int i = data.length - 1; i >= 0; i--) {
            int temp = data[0];
            data[0] = data[i];
            data[i] = temp;

            // reduce the heap window by 1
            size = size - 1;

            // call max heap on the reduced heap
            heapify(0, data, size);
        }
    }

    private int leftChild(int i) {
        return 2 * i + 1;
    }

    private int rightChild(int i) {
        return 2 * i + 2;
    }

    private void heapify(int i, int[] data, int size) {
        int largestElementIndex = i;

        int leftChildIndex = leftChild(i);
        if (leftChildIndex < size && data[leftChildIndex] > data[largestElementIndex]) {
            largestElementIndex = leftChildIndex;
        }

        int rightChildIndex = rightChild(i);
        if (rightChildIndex < size && data[rightChildIndex] > data[largestElementIndex]) {
            largestElementIndex = rightChildIndex;
        }

        if (largestElementIndex != i) {
            int swap = data[i];
            data[i] = data[largestElementIndex];
            data[largestElementIndex] = swap;

            // Recursively "heapify"
            heapify(largestElementIndex, data, size);
        }
        findHighest();
        repaint();
        try {
            Thread.sleep((long) (sortSpeed));
        } catch (InterruptedException Ex) {
        }
    }

    public void run() {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        while (true) {
            if (bubble) {
                running = "running Bubble Sort...";
                bubble();
                running = "Bubble Sort complete!";
            }
            if (selection) {
                running = "running Selection Sort...";
                selection();
                running = "Selection Sort complete!";
            }
            if (insertion) {
                running = "running Insertion Sort...";
                insertion();
                running = "Insertion Sort complete!";
            }
            if (radixSort) {
                running = "running Radix Sort...";
                radix();
                running = "Radix Sort complete!";
            }
            if (heap) {
                running = "running Heap Sort...";
                heap();
                running = "Heap Sort complete!";
            }
            repaint();
            try {
                Thread.sleep((long) (50));
            } catch (InterruptedException Ex) {
            }
        }
    }

    public void start() {
        Thread th = new Thread(this);
        th.start();
        setFocusable(true);
    }

    public void stop() {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        createBricks();
        running = "";
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        char val = e.getKeyChar();
        if (val == 'b') {
            bubble = true;
        }
        if (val == 's') {
            selection = true;
        }
        if (val == 'i') {
            insertion = true;
        }
        if (val == 'r') {
            radixSort = true;
        }
        if (val == 'h') {
            heap = true;
        }
        if (val == 'e') {
            System.exit(0);
        }
        if (val == '0') {
            sortSpeed = 1;
            running = "speed set to Very Fast";
        }
        if (val == '1') {
            sortSpeed = 5;
            running = "speed set to Fast";
        }
        if (val == '2') {
            sortSpeed = 10;
            running = "speed set to Normal";
        }
        if (val == '3') {
            sortSpeed = 25;
            running = "speed set to Slow";
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }
}

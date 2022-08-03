package fm.icelink.chat.websync4;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DLinkedList {
    class DLinkedNode {
        String key;
        int value;
        DLinkedNode prev;
        DLinkedNode next;
    }

    private void addNode(DLinkedNode node) {
        /**
         * Always add the new node right after head.
         */
        node.prev = head;
        node.next = head.next;

        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(DLinkedNode node){
        /**
         * Remove an existing node from the linked list.
         */
        DLinkedNode prev = node.prev;
        DLinkedNode next = node.next;

        prev.next = next;
        next.prev = prev;
    }

    private void moveToHead(DLinkedNode node){
        /**
         * Move certain node in between to the head.
         */
        removeNode(node);
        addNode(node);
    }

    private DLinkedNode popTail() {
        /**
         * Pop the current tail.
         */
        DLinkedNode res = tail.prev;
        removeNode(res);
        return res;
    }

    private Map<String, DLinkedNode> cache = new HashMap<>();
    private int size;
    private int capacity;
    private DLinkedNode head, tail;

    public DLinkedList(int capacity) {
        this.size = 0;
        this.capacity = capacity;

        head = new DLinkedNode();
        // head.prev = null;

        tail = new DLinkedNode();
        // tail.next = null;

        head.next = tail;
        tail.prev = head;
    }

    public Set<String> getCurrent() {
        int cnt = 0;
        DLinkedNode curr = head.next;
        Set<String> res = new HashSet<>();
        while (cnt < size) {
            res.add(curr.key);
            curr = curr.next;
            cnt++;
        }
        return res;
    }

    public Set<String> getUnneeded() {
        Set<String> res = new HashSet<>();
        while (size > capacity) {
            String tmp = removeLast();
            if (!tmp.equals("")) res.add(tmp);
        }
        return res;
    }

    public void removeAll() {
        while (size > 0) {
            removeLast();
        }
    }

    private String removeLast() {
        if (tail.prev == head) return "";
        DLinkedNode tail = popTail();
        String res =  tail.key;
        cache.remove(tail.key);
        --size;
        return res;
    }

    public void remove(String str) {
        if (!cache.containsKey(str)) return;
        DLinkedNode tmp = cache.get(str);
        cache.remove(str);
        size--;
        Log.d("dLinkedList", "remove " + str + "successfully");
        removeNode(tmp);
    }

    public int get(String key) {
        DLinkedNode node = cache.get(key);
        if (node == null) return -1;

        // move the accessed node to the head;
        moveToHead(node);

        return node.value;
    }

    public void put(String key, int value) {
        DLinkedNode node = cache.get(key);

        if(node == null) {
            DLinkedNode newNode = new DLinkedNode();
            newNode.key = key;
            newNode.value = value;

            cache.put(key, newNode);
            addNode(newNode);

            ++size;
            Log.d("dLinkedList", "add " + key + " successfully, size: " + String.valueOf(size));
            /*
            if(size > capacity) {
                // pop the tail
                DLinkedNode tail = popTail();
                cache.remove(tail.key);
                --size;
            }
             */
        } else {
            // update the value.
            node.value = value;
            moveToHead(node);
        }

    }
}
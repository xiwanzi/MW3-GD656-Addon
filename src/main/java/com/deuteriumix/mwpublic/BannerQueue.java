package com.deuteriumix.mwpublic;

import java.util.ArrayDeque;

/** Bounded by the amount of feedback still visible to the player, not the server tick rate. */
final class BannerQueue {
    record Entry(String label,String slot,int combo,int distinction) {}
    private final ArrayDeque<Entry> pending=new ArrayDeque<>(3);
    private boolean crowded;
    private Entry playing;
    private int shownCombo;
    private int shownDistinction;

    void offer(Entry e){
        if(!crowded && pending.size()+(playing==null?0:1)<3){
            pending.addLast(e);
            return;
        }
        crowded=true;
        // Protect the opening if several packets arrive before the first render frame.
        Entry opening=playing==null?pending.pollFirst():null;
        Entry bestCombo=null,bestSpecial=null;
        pending.addLast(e);
        for(Entry item:pending){
            if(item.equals(opening) || item.equals(playing))continue;
            if(item.combo>shownCombo && (bestCombo==null || item.combo>bestCombo.combo))bestCombo=item;
            if(item.distinction>shownDistinction && (bestSpecial==null || item.distinction>=bestSpecial.distinction))bestSpecial=item;
        }
        pending.clear();
        if(opening!=null)pending.addLast(opening);
        if(bestCombo!=null)pending.addLast(bestCombo);
        if(bestSpecial!=null)pending.addLast(bestSpecial);
    }
    Entry next(){
        playing=pending.pollFirst();
        if(playing==null){clear();return null;}
        shownCombo=Math.max(shownCombo,playing.combo);
        shownDistinction=Math.max(shownDistinction,playing.distinction);
        return playing;
    }
    boolean empty(){return pending.isEmpty();}
    void clear(){pending.clear();playing=null;crowded=false;shownCombo=shownDistinction=0;}
}

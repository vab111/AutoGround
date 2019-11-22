package com.example.autoground;

import android.graphics.Point;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.PortUnreachableException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class RecordInfor {
    public Point pointA = new Point(0,0);
    public Point pointB = new Point(0,0);
    public int Kuan;
    public List<route> list = new List<route>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(@Nullable Object o) {
            return false;
        }

        @NonNull
        @Override
        public Iterator<route> iterator() {
            return null;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] ts) {
            return null;
        }

        @Override
        public boolean add(route route) {
            return false;
        }

        @Override
        public boolean remove(@Nullable Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> collection) {
            return false;
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends route> collection) {
            return false;
        }

        @Override
        public boolean addAll(int i, @NonNull Collection<? extends route> collection) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> collection) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> collection) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public route get(int i) {
            return null;
        }

        @Override
        public route set(int i, route route) {
            return null;
        }

        @Override
        public void add(int i, route route) {

        }

        @Override
        public route remove(int i) {
            return null;
        }

        @Override
        public int indexOf(@Nullable Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(@Nullable Object o) {
            return 0;
        }

        @NonNull
        @Override
        public ListIterator<route> listIterator() {
            return null;
        }

        @NonNull
        @Override
        public ListIterator<route> listIterator(int i) {
            return null;
        }

        @NonNull
        @Override
        public List<route> subList(int i, int i1) {
            return null;
        }
    };
}

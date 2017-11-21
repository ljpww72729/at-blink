package com.ljpww72729.wilddog.ui.database;


import com.wilddog.client.DataSnapshot;

public interface SnapshotParser<T> {
    /**
     * This method parses the DataSnapshot into the requested type.
     *
     * @param snapshot the DataSnapshot to extract the model from
     * @return the model extracted from the DataSnapshot
     */
    T parseSnapshot(DataSnapshot snapshot);
}

package com.ngocrong.backend.network;

import com.ngocrong.backend.character.Char;

public interface IService {
    void setChar(Char _char);

    void close();

    void setResource();
}

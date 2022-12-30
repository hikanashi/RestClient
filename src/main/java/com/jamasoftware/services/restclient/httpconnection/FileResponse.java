package com.jamasoftware.services.restclient.httpconnection;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class FileResponse extends Response {
    private byte[] fileBytes;

    public FileResponse(int statusCode, InputStream inputStream) throws IOException {
        super(statusCode, "File response");
        this.fileBytes = IOUtils.toByteArray(inputStream);
    }

    public byte[] getFileData() {
        return fileBytes;
    }
}

package org.planqk.atlas.core.services;

import java.io.IOException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.exceptions.CloudStorageException;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.repository.FileRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;

import lombok.RequiredArgsConstructor;

@Service
@Profile("google-cloud")
@RequiredArgsConstructor
public class FileServiceCloudStorageImpl implements FileService {

    private final Storage storage;

    @Value("${cloud.storage.implementation-files-bucket-name}")
    private String implementationFilesBucketName;

    private final FileRepository fileRepository;

    private final ImplementationRepository implementationRepository;

    @Override
    public File create(UUID implementationId, MultipartFile file) {
        try {
            final BlobId blobId = BlobId.of(implementationFilesBucketName, implementationId + "/" + file.getOriginalFilename());
            final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            final Blob blob = storage.create(blobInfo, file.getBytes());
            final File implementationFile = getFileFromBlob(blob);

            // set the name to the original file, as the name from the blob includes the implementationId
            implementationFile.setName(file.getOriginalFilename());

            // check if file already exists. If so set the Id to avoid duplicates in DB
            fileRepository.findByFileURL(implementationFile.getFileURL())
                .ifPresent(persistedFile -> implementationFile.setId(persistedFile.getId()));

            final Implementation implementation = ServiceUtils.findById(implementationId, Implementation.class, implementationRepository);
            implementationFile.setImplementation(implementation);
            return fileRepository.save(implementationFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read contents of multipart file");
        } catch (StorageException e) {
            throw new CloudStorageException("Could not create file in storage");
        }
    }

    @Override
    public File findById(UUID id) {
        return ServiceUtils.findById(id, File.class, fileRepository);
    }

    @Override
    public Page<File> findAllByImplementationId(UUID implementationId, Pageable pageable) {
        return fileRepository.findFilesByImplementation_Id(implementationId, pageable);
    }

    @Override
    public byte[] getFileContent(UUID id) {
        final File file = ServiceUtils.findById(id, File.class, fileRepository);
        try {
            final BlobId blobId = BlobId.of(implementationFilesBucketName, file.getFileURL());
            final Blob blob = this.storage.get(blobId);
            if (blob == null) {
                throw new NoSuchElementException("File with URL \"" + file.getFileURL() + "\" does not exist");
            }
            return blob.getContent();
        } catch (StorageException e) {
            throw new CloudStorageException("Could not get file from storage");
        }
    }

    @Override
    public File update(UUID id, MultipartFile file) {
        return null;
    }

    @Override
    public void delete(UUID id) {
        final File storedEntity = this.findById(id);
        final BlobId blobId = BlobId.of(implementationFilesBucketName, storedEntity.getFileURL());
        try {
            storage.delete(blobId);
            this.fileRepository.delete(storedEntity);
        } catch (StorageException e) {
            throw new CloudStorageException("Could not delete file from storage");
        }
    }

    private File getFileFromBlob(Blob blob) {
        final File file = new File();
        file.setName(blob.getName());
        file.setMimeType(blob.getContentType());
        file.setFileURL(blob.getName());
        file.setCreationDate(new Date(blob.getCreateTime()));
        file.setLastModifiedAt(new Date(blob.getUpdateTime()));
        return file;
    }
}
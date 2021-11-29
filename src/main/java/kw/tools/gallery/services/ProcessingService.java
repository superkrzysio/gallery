package kw.tools.gallery.services;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.processing.DirCrawler;
import kw.tools.gallery.processing.ImageAccessor;
import kw.tools.gallery.processing.Task;
import kw.tools.gallery.processing.ThumbnailingTaskFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessingService
{
    public enum ProcessingStatus
    {
        SUCCESSFUL,     // when all jobs are finished and successful
        ERRORS,         // when all jobs are finished but there are errors
        WORKING,        // still not all jobs finished
        IDLE,           // there are jobs to do, but nothing is happening - broken?
        UNEXPECTED  // combination of task statuses unexpected
    }

    @Autowired
    private DirCrawler dirCrawler;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private ImageAccessor imageAccessor;

    @Autowired
    private ThumbnailingTaskFactory thumbnailingTaskFactory;

    public List<GalleryFolderDTO> fetchGalleries(String inPath)
    {
        List<GalleryFolderDTO> result = new ArrayList<>();

        try
        {
            dirCrawler.forEach(inPath, galPath -> {
                if (imageAccessor.getImages(galPath.toString()).isEmpty())
                {
                    // System.out.println("Found empty path: " + galPath);  // todo: logger
                    return;
                }
                GalleryFolderDTO dto = new GalleryFolderDTO();
                dto.filename = galPath.getFileName().toString();
                dto.fullPath = galPath.toString();
                dto.pictureCount = imageAccessor.getImages(galPath.toString()).size();
                result.add(dto);
            });
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public void generate(String repositoryId, String galleryId, String galleryPath)
    {
        taskService.execute(thumbnailingTaskFactory.create(
                repositoryId,
                galleryPath,
                cacheUtils.generateGalleryDir(repositoryId, galleryId)
        ));
    }

    public ProcessingStatus getProcessingStatus(String repositoryId)
    {
        List<Task.Status> statuses = taskService.getStatuses(repositoryId);
        StatusSummary summary = new StatusSummary(
                statuses.stream().filter(s -> s == Task.Status.CREATED).count(),
                statuses.stream().filter(s -> s == Task.Status.FINISHED).count(),
                statuses.stream().filter(s -> s == Task.Status.ERROR).count(),
                statuses.stream().filter(s -> s == Task.Status.WORKING).count()
        );

        if (isEverythingSuccessful(summary))
        {
            return ProcessingStatus.SUCCESSFUL;
        }

        if (isFinishedButErrors(summary))
        {
            return ProcessingStatus.ERRORS;
        }

        if (isWorkInProgress(summary))
        {
            return ProcessingStatus.WORKING;
        }

        if (isProcessingIdle(summary))
        {
            return ProcessingStatus.IDLE;
        }
        return ProcessingStatus.UNEXPECTED;
    }

    public int getFinishedCount(String repoId)
    {
        return (int) taskService.getByCategory(repoId).stream().filter(
                t -> t.getStatus() == Task.Status.FINISHED || t.getStatus() == Task.Status.ERROR
        ).count();
    }

    private boolean isEverythingSuccessful(StatusSummary summary)
    {
        return summary.created == 0 && summary.inProgress == 0 && summary.error == 0;
    }

    private boolean isFinishedButErrors(StatusSummary summary)
    {
        return summary.created == 0 && summary.finished == 0 && summary.error > 0;
    }

    private boolean isWorkInProgress(StatusSummary summary)
    {
        return summary.inProgress > 0;
    }

    private boolean isProcessingIdle(StatusSummary summary)
    {
        return summary.created > 0 && summary.inProgress == 0;
    }

    public static class GalleryFolderDTO
    {
        public String filename;
        public String fullPath;
        public Integer pictureCount;
    }

    private static class StatusSummary
    {
        long created, finished, error, inProgress;

        StatusSummary(long created, long finished, long error, long inProgress)
        {
            this.created = created;
            this.finished = finished;
            this.error = error;
            this.inProgress = inProgress;
        }

        @Override
        public String toString()
        {
            return "StatusSummary{" +
                    "created=" + created +
                    ", finished=" + finished +
                    ", error=" + error +
                    ", inProgress=" + inProgress +
                    '}';
        }
    }
}

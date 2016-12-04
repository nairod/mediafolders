package ch.flask.mediafolders;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.LocalDateTime;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public final class FotoMetaData extends MediaMetaData {
    private static final GeoApiContext CONTEXT = new GeoApiContext().setApiKey(MediaFolders.PROPS.googleApiKey());
    private static final Logger LOGGER = Logger.getLogger(FotoMetaData.class.getName());
    private static final String UNKNOWN_PLACE = "unbekannter_Ort";
    private final Metadata metadata;
    
    public FotoMetaData(File file) {
    	super(file);
		metadata = createMetadata();
	}
    
    String getPlace() {
        String place = null;
        Optional<GeoLocation> geoLocation = getGeolocation();
        if (geoLocation.isPresent()) {
            GeocodingResult[] results;
            try {
                results = queryGeoLocation(geoLocation.get());
                place = results[0].addressComponents[2].longName;
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "can't get place", ex);
            }
        }
        if (place == null) {
            place = UNKNOWN_PLACE;
        }
        return place;
    }
    
    String getYear() {
        if (getDate() != null) {
            return String.valueOf(LocalDateTime.fromDateFields(getDate()).getYear());
        } else {
            return String.valueOf(getYearOfFile());
        }
    }
    
    Date getDate() {
        if (directory().isPresent()) {
            return directory().get().getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
        } else {
            return null;
        }
    }
    
    private Optional<GeoLocation> getGeolocation() {
        GeoLocation geoLocation = null;
        if (metadata == null) {
            return Optional.empty();
        }
        Collection<GpsDirectory> gpsDirectories;
        gpsDirectories = metadata.getDirectoriesOfType(GpsDirectory.class);

        if (gpsDirectories != null) {
            for (GpsDirectory gpsDirectory : gpsDirectories) {
                geoLocation = gpsDirectory.getGeoLocation();
            }
        }
        return Optional.ofNullable(geoLocation);
    }

    private GeocodingResult[] queryGeoLocation(GeoLocation geoLocation) throws Exception {
        return GeocodingApi.newRequest(CONTEXT)
                .latlng(new LatLng(geoLocation.getLatitude(), geoLocation.getLongitude()))
                .await();
    }
    
    private Metadata createMetadata() {
        Metadata metaData = null;
        try {
            metaData = ImageMetadataReader.readMetadata(file());
        } catch (ImageProcessingException | IOException ex) {
            LOGGER.log(Level.SEVERE, "can't read metadata", ex);
        }
        return metaData;
    }

    private Optional<Directory> directory() {
        if (metadata == null) {
            return Optional.empty();
        }
        ExifSubIFDDirectory firstDirectoryOfType = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        return firstDirectoryOfType != null ? Optional.of(firstDirectoryOfType) : Optional.empty();
    }

}

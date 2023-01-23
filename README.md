# MGRS Java

#### Military Grid Reference System Lib ####

The MGRS Library was developed at the [National Geospatial-Intelligence Agency (NGA)](http://www.nga.mil/) in collaboration with [BIT Systems](https://www.caci.com/bit-systems/). The government has "unlimited rights" and is releasing this software to increase the impact of government investments by providing developers with the opportunity to take things in new directions. The software use, modification, and distribution rights are stipulated within the [MIT license](http://choosealicense.com/licenses/mit/).

### Pull Requests ###
If you'd like to contribute to this project, please make a pull request. We'll review the pull request and discuss the changes. All pull request contributions to this project will be released under the MIT license.

Software source code previously released under an open source license and then modified by NGA staff is considered a "joint work" (see 17 USC § 101); it is partially copyrighted, partially public domain, and as a whole is protected by the copyrights of the non-government authors and must be released according to the terms of the original open source license.

### About ###

[MGRS](http://ngageoint.github.io/mgrs-java/) is a Java library providing Military Grid Reference System functionality, a geocoordinate standard used by NATO militaries for locating points on Earth.

### Usage ###

View the latest [Javadoc](http://ngageoint.github.io/mgrs-java/docs/api/)

#### Coordinates ####

```java

MGRS mgrs = MGRS.parse("33XVG74594359");
Point point = mgrs.toPoint();
Point pointMeters = point.toMeters();
UTM utm = mgrs.toUTM();
String utmCoordinate = utm.toString();
Point point2 = utm.toPoint();

MGRS mgrs2 = MGRS.parse("33X VG 74596 43594");

double latitude = 63.98862388;
double longitude = 29.06755082;
Point point3 = Point.point(longitude, latitude);
MGRS mgrs3 = MGRS.from(point3);
String mgrsCoordinate = mgrs3.toString();
String mgrsGZD = mgrs3.coordinate(GridType.GZD);
String mgrs100k = mgrs3.coordinate(GridType.HUNDRED_KILOMETER);
String mgrs10k = mgrs3.coordinate(GridType.TEN_KILOMETER);
String mgrs1k = mgrs3.coordinate(GridType.KILOMETER);
String mgrs100m = mgrs3.coordinate(GridType.HUNDRED_METER);
String mgrs10m = mgrs3.coordinate(GridType.TEN_METER);
String mgrs1m = mgrs3.coordinate(GridType.METER);

UTM utm2 = UTM.from(point3);
MGRS mgrs4 = utm2.toMGRS();

UTM utm3 = UTM.parse("18 N 585628 4511322");
MGRS mgrs5 = utm3.toMGRS();

```

#### Draw Tile Template ####

See [mgrs-android](https://github.com/ngageoint/mgrs-android) for a concrete example

```java

// GridTile tile = ...;

Grids grids = Grids.create();

ZoomGrids zoomGrids = grids.getGrids(tile.getZoom());
if (zoomGrids.hasGrids()) {

  GridRange gridRange = GridZones.getGridRange(tile.getBounds());

  for (Grid grid : zoomGrids) {

    // draw this grid for each zone
    for (GridZone zone : gridRange) {

      List<GridLine> lines = grid.getLines(tile, zone);
      if (lines != null) {
        PixelRange pixelRange = zone.getBounds()
            .getPixelRange(tile);
        for (GridLine line : lines) {
          Pixel pixel1 = line.getPoint1().getPixel(tile);
          Pixel pixel2 = line.getPoint2().getPixel(tile);
          // Draw line
        }
      }

      List<GridLabel> labels = grid.getLabels(tile, zone);
      if (labels != null) {
        for (GridLabel label : labels) {
          PixelRange pixelRange = label.getBounds()
              .getPixelRange(tile);
          Pixel centerPixel = label.getCenter()
              .getPixel(tile);
          // Draw label
        }
      }

    }
  }
}

```

#### Properties ####

Default grid properties including zoom ranges, styles, and labelers are defined in [mgrs.properties](https://github.com/ngageoint/mgrs-java/blob/master/src/main/resources/mgrs.properties). The defaults can be changed in code by modifying the [Grids](https://github.com/ngageoint/mgrs-java/blob/master/src/main/java/mil/nga/mgrs/grid/Grids.java).

### Installation ###

Pull from the [Maven Central Repository](http://search.maven.org/#artifactdetails|mil.nga|mgrs|2.1.2|jar) (JAR, POM, Source, Javadoc)

    <dependency>
        <groupId>mil.nga</groupId>
        <artifactId>mgrs</artifactId>
        <version>2.1.2</version>
    </dependency>

### Build ###

[![Build & Test](https://github.com/ngageoint/mgrs-java/workflows/Build%20&%20Test/badge.svg)](https://github.com/ngageoint/mgrs-java/actions/workflows/build-test.yml)

Build this repository using Eclipse and/or Maven:

    mvn clean install

### Remote Dependencies ###

* [Grid Java](https://github.com/ngageoint/grid-java) (The MIT License (MIT)) - Grid Library

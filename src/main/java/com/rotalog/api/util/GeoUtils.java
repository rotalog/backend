package com.rotalog.api.util;

import org.locationtech.jts.geom.*;

/**
 * Utilitários para criação e manipulação de tipos geográficos PostGIS.
 *
 * Todos os objetos usam SRID 4326 (WGS84), que é o padrão de GPS /
 * Google Maps. Coordenadas sempre no formato (longitude, latitude).
 */
public final class GeoUtils {

    /** SRID 4326 = WGS84 (padrão GPS / Google Maps) */
    public static final int SRID = 4326;

    private static final GeometryFactory FACTORY =
            new GeometryFactory(new PrecisionModel(), SRID);

    private GeoUtils() {}

    /**
     * Cria um ponto geográfico a partir de latitude e longitude.
     *
     * Uso:
     *   Point p = GeoUtils.createPoint(-3.1190, -60.0217); // Manaus-AM
     *   customer.setLocation(p);
     */
    public static Point createPoint(double latitude, double longitude) {
        Coordinate coord = new Coordinate(longitude, latitude);
        Point point = FACTORY.createPoint(coord);
        point.setSRID(SRID);
        return point;
    }

    /**
     * Cria uma rota (LineString) a partir de uma lista de pontos [lat, lng].
     *
     * Uso:
     *   LineString rota = GeoUtils.createLineString(new double[][]{
     *       {-3.1190, -60.0217},  // ponto de origem (Manaus)
     *       {-3.7327, -60.3500},  // ponto intermediário
     *       {-3.8397, -60.6719}   // destino
     *   });
     *   order.setDeliveryRoute(rota);
     */
    public static LineString createLineString(double[][] latLngPairs) {
        Coordinate[] coords = new Coordinate[latLngPairs.length];
        for (int i = 0; i < latLngPairs.length; i++) {
            coords[i] = new Coordinate(latLngPairs[i][1], latLngPairs[i][0]);
        }
        LineString line = FACTORY.createLineString(coords);
        line.setSRID(SRID);
        return line;
    }

    /**
     * Extrai latitude de um Point.
     */
    public static double getLatitude(Point point) {
        return point.getY();
    }

    /**
     * Extrai longitude de um Point.
     */
    public static double getLongitude(Point point) {
        return point.getX();
    }
}

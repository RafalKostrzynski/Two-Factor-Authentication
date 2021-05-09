package pl.kostrzynski.twofactorauthentication.model.utility;

import okhttp3.tls.Certificates;

import java.security.cert.X509Certificate;

public class Resources {

    private static final String HOST_ADDRESS = "192.168.178.119";

    private static final X509Certificate CERTIFICATE_PEM = Certificates.decodeCertificatePem("-----BEGIN CERTIFICATE-----\n" +
            "MIIDdTCCAl2gAwIBAgIIeiGsWQWp2t8wDQYJKoZIhvcNAQELBQAwaTELMAkGA1UE\n" +
            "BhMCREUxEDAOBgNVBAgTB0hhbWJ1cmcxEDAOBgNVBAcTB0hhbWJ1cmcxDDAKBgNV\n" +
            "BAoTAzJGQTEMMAoGA1UECxMDMkZBMRowGAYDVQQDExFSYWZhbCBLb3N0cnp5bnNr\n" +
            "aTAeFw0yMTA0MDUxMTM0MjRaFw0zMTA0MDMxMTM0MjRaMGkxCzAJBgNVBAYTAkRF\n" +
            "MRAwDgYDVQQIEwdIYW1idXJnMRAwDgYDVQQHEwdIYW1idXJnMQwwCgYDVQQKEwMy\n" +
            "RkExDDAKBgNVBAsTAzJGQTEaMBgGA1UEAxMRUmFmYWwgS29zdHJ6eW5za2kwggEi\n" +
            "MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCaHdqGapQU2sz7F0OZwwTFpWaI\n" +
            "GhDSoA47cNhiKUrybeX9PZv/rjINzRH0a6jvKOoqDh+D3Gt+VMb/KTwZJsp4LJ4v\n" +
            "n/yBiRfDfi8iQqn4MvXf2GUE9oHwTMHFSYZ0zb7rR4Yi6YmVrPKLBv+R7eGuLjYb\n" +
            "xxPf5eNsbKqV15U5qSBg7obqaZ5t6WHbN23FPhv0kMlKnlyCdJCgvH4NBhH4T2Yz\n" +
            "x2clb7UKJyiHrOkNsN+/uhjYrFXZHCrYfaSxJj4hD6phStQzSEm61RhVN1vt9ew4\n" +
            "btWrqejuq5IYRtksXrRGq1bjHyUcTHVr843YU2ueaKDXTajUw+KXHcmXnUN/AgMB\n" +
            "AAGjITAfMB0GA1UdDgQWBBQBYt9SgiWzlMbdS5g4XlM40Us/ezANBgkqhkiG9w0B\n" +
            "AQsFAAOCAQEANFEPNukIgBP5+DvHiKIIHupDcUFxzYxUWd00EXZdwM6RjVK+P+EC\n" +
            "kqJpjDzXQqk3EToqtS1ojVOHouVaGy3F3agq5EJ2qhyDUd4358QemoTexuiiA5Lg\n" +
            "D/ycnp/aTVTKO5ThxbNGhA+16LDRq7RmPdbabHxfB3bxQrGT1ZDNhxxlN9VDIt0R\n" +
            "RPBfg0mbEO90v6jrimTkhISHhhOf2LrP3XMu7v4AetJtvE+w2J6FVa2VR0AyYabf\n" +
            "iCHRkfLFKUoDvHi3Q9EGBxNW8PS+/sJAtGnu53XPIRwHM0v9dQkbb0ULQL6DI2dj\n" +
            "qqh5/vZPcDkQ7AwhoTt27ym1WTUbvwCgyg==\n" +
            "-----END CERTIFICATE-----\n");

    private static final String[] ADJECTIVES = {
            "the same", "beautiful", "Certain", "The original", "beautiful", "happy", "impossible", "Lovely",
            "understandable", "big", "later", "important", "frequent", "Nature", "truly", "scared", "Aerial",
            "Red", "painful", "Clean", "hard", "wonderful", "happy", "improving", "Influential", "Yellow",
            "Dear", "basic", "perfect", "Golden", "clever", "fresh", "charming", "bright", "common",
            "direct", "real", "Heard", "effortful", "Fast", "Snow White", "hurry", "optimistic", "Main",
            "Vivid", "ice cold", "Observant", "Amazing", "horizontal", "touching", "blue", "Ignorant", "Polite",
            "warm", "Affectionate", "normal", "plain", "bright", "Lagging", "generous", "Boss", "Hard work",
            "Clear", "professional", "not permanent", "Atmospheric", "Confidante", "Just right", "relatively",
            "Peaceful", "friendly", "Huge", "Beautiful", "Daily", "Advanced", "identical", "straight", "Stable",
            "satisfied", "Sturdy", "Long time", "obedient", "famous", "Sultry", "Many", "Crowded", "intrinsic",
            "Tiny", "honest", "Friendly", "original", "Ridiculous", "qualified", "private", "Big Red", "Powerful", "Clean",
            "dim", "Bright red", "Pink", "frightening", "extra", "Beautiful", "busy", "Cold", "Enthusiastic", "Empty",
            "Desolate", "public", "Cold", "Complete", "Grass Green", "Competent", "Furious", "Heartful", "Amateur",
            "hollow", "cool", "Long term", "Natural", "Reconciliation", "legal", "Ming Jing", "not Outdated", "Low",
            "Unpleasant", "high level", "Used", "Uncertain", "not Public", "hardworking", "Little", "Busy", "Daily",
            "Important", "rare", "Non-divided", "Feared of people", "Busy", "happy", "special", "Future", "great",
            "difficult", "sad", "actual", "realistic", "abundant", "same", "huge", "patiently", "superior",
            "Dear", "Nasty", "severe", "positive", "neat", "Environmentally Friendly"};

    public static String[] getAdjectives() {
        return ADJECTIVES;
    }

    public static X509Certificate getCertificate() {
        return CERTIFICATE_PEM;
    }

    public static String getHostAddress() {
        return HOST_ADDRESS;
    }

}

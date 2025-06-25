package src;
import java.time.LocalDate;
import java.io.Serializable;
import java.util.HashMap;

public class Contacto implements Serializable, Comparable<Contacto> {
    private String id;
    private String type; // "persona" o "empresa"
    private CircularDoubleLinkedList<Attribute> attributes;
    private CircularDoubleLinkedList<Photo> photos;
    private CircularDoubleLinkedList<Contacto> relatedContacts;
    private HashMap<String, String> attributeMap;
    private Photo currentPhoto; // Foto actual del contacto

    public Contacto(String id, String type) {
        this.id = id;
        this.type = type;
        this.attributes = new CircularDoubleLinkedList<>();
        this.photos = new CircularDoubleLinkedList<>();
        this.relatedContacts = new CircularDoubleLinkedList<>();
        this.attributeMap = new HashMap<>();
        this.currentPhoto = null;
    }

    // Plantillas para persona y empresa
    public static Contacto createPerson(String id, String firstName, String lastName, LocalDate birthday) {
        Contacto person = new Contacto(id, "persona");
        person.addAttribute("nombre", firstName);
        person.addAttribute("apellido", lastName);
        person.addAttribute("cumpleaños", birthday.toString());
        return person;
    }

    public static Contacto createCompany(String id, String name, String address) {
        Contacto company = new Contacto(id, "empresa");
        company.addAttribute("nombre", name);
        company.addAttribute("dirección", address);
        return company;
    }

    // Métodos para atributos
    public void addAttribute(String key, String value) {
        Attribute attr = new Attribute(key, value);
        attributes.add(attr);
        attributeMap.put(key, value);
    }

    public void removeAttribute(String key) {
        Attribute attrToRemove = null;
        for (Attribute attr : attributes) {
            if (attr.getKey().equals(key)) {
                attrToRemove = attr;
                break;
            }
        }
        if (attrToRemove != null) {
            attributes.remove(attrToRemove);
            attributeMap.remove(key);
        }
    }

    // Métodos para fotos
    public void addPhoto(String photoPath) {
        Photo photo = new Photo(photoPath);
        photos.add(photo);
        currentPhoto = photo; // La nueva foto se convierte en la actual
    }

    public void removePhoto(String photoPath) {
        Photo photoToRemove = null;
        for (Photo photo : photos) {
            if (photo.getPath().equals(photoPath)) {
                photoToRemove = photo;
                break;
            }
        }
        if (photoToRemove != null) {
            photos.remove(photoToRemove);
            if (photoToRemove.equals(currentPhoto)) {
                currentPhoto = photos.getTamanio() > 0 ? photos.siguiente() : null;
            }
        }
    }

    public void editPhoto(String oldPath, String newPath) {
        for (Photo photo : photos) {
            if (photo.getPath().equals(oldPath)) {
                photo.setPath(newPath);
                if (photo.equals(currentPhoto)) {
                    currentPhoto = photo;
                }
                break;
            }
        }
    }

    public Photo getCurrentPhoto() {
        return currentPhoto;
    }

    // Métodos para contactos relacionados
    public void addRelatedContact(Contacto contact) {
        relatedContacts.add(contact);
    }

    public void removeRelatedContact(Contacto contact) {
        relatedContacts.remove(contact);
    }

    // Implementación de Comparable para ordenar por apellido/nombre
    @Override
    public int compareTo(Contacto other) {
        String thisName = attributeMap.getOrDefault("apellido", "") + attributeMap.getOrDefault("nombre", "");
        String otherName = other.attributeMap.getOrDefault("apellido", "") + other.attributeMap.getOrDefault("nombre", "");
        return thisName.compareTo(otherName);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public CircularDoubleLinkedList<Attribute> getAttributes() {
        return attributes;
    }

    public CircularDoubleLinkedList<Photo> getPhotos() {
        return photos;
    }

    public CircularDoubleLinkedList<Contacto> getRelatedContacts() {
        return relatedContacts;
    }

    public String getAttributeValue(String key) {
        return attributeMap.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contacto contacto = (Contacto) o;
        return id.equals(contacto.id);
    }
}
public interface HealthEntity extends Executioner {
    int getHealth();
    void takeDamage(int damage);
}

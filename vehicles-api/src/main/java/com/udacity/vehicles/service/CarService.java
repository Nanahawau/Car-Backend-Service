package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.Price;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository carRepository;
    private final MapsClient mapsClient;
    private final PriceClient priceClient;

    public CarService(CarRepository carRepository, WebClient pricing, WebClient maps, ModelMapper modelMapper) {
        this.mapsClient = new MapsClient(maps, modelMapper);
        this.priceClient = new PriceClient(pricing);
        this.carRepository = carRepository;
    }


    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return carRepository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {

        if (!carRepository.findById(id).isPresent()) {
            throw new CarNotFoundException();
        }

        Car car = carRepository.findCarById(id);

        String price  = priceClient.getPrice(id);

        System.out.println("price:::" + price);

        car.setPrice(price);


        Location location = mapsClient.getAddress(new Location(car.getLocation().getLat(), car.getLocation().getLon()));

        System.out.println("location" + location);

        car.setLocation(location);

        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return carRepository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setCondition(car.getCondition());
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return carRepository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return carRepository.save(car);
    }


    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {

        if (!carRepository.findById(id).isPresent()) {
            throw new CarNotFoundException();
        }

        carRepository.delete(carRepository.findCarById(id));

    }
}

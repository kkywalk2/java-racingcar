package com.nextlevel.kky.racing.service;

import com.nextlevel.kky.racing.core.Car;
import com.nextlevel.kky.racing.core.CarNameValidator;
import com.nextlevel.kky.racing.core.CarRacingExecutor;
import com.nextlevel.kky.racing.core.RandomIntegerGenerator;
import com.nextlevel.kky.racing.dto.CarRaceResultDto;
import com.nextlevel.kky.racing.dto.CarRaceResultDto.CarPositionDto;
import com.nextlevel.kky.racing.dto.CarRaceResultDto.RoundResultDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CarRacingService {

    private final CarService carService;

    public CarRacingService() {
        this.carService = new CarService(new RandomIntegerGenerator());
    }

    public CarRacingService(CarService carService) {
        this.carService = carService;
    }

    public CarRaceResultDto race(String[] carNames, int roundCount) {
        if (!CarNameValidator.checkValidation(carNames)) {
            throw new RuntimeException("유효하지 않은 이름 입니다!");
        }

        List<RoundResultDto> roundResultDtoList = new ArrayList<>();

        List<Car> carList = carService.createCars(carNames);
        CarRacingExecutor carRacingExecutor = new CarRacingExecutor(carList);
        roundResultDtoList.add(getCurrentStatus(carRacingExecutor));

        for (; roundCount > 0; roundCount--) {
            carRacingExecutor.proceedNextRound();
            roundResultDtoList.add(getCurrentStatus(carRacingExecutor));
        }

        return new CarRaceResultDto(roundResultDtoList);
    }

    private RoundResultDto getCurrentStatus(CarRacingExecutor carRacingExecutor) {
        List<CarPositionDto> carPositionDtoList = carRacingExecutor.getCarList().stream()
                .map(car -> new CarPositionDto(car.getName(), car.getCurrentPosition()))
                .collect(Collectors.toList());

        return new RoundResultDto(carPositionDtoList);
    }
}

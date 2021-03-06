package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import static org.firstinspires.ftc.teamcode.actions.LiftActions.*;
import static org.firstinspires.ftc.teamcode.actions.GenericActions.*;
import static org.firstinspires.ftc.teamcode.actions.GenericActions.GenActCond.*;
import static org.firstinspires.ftc.teamcode.actions.LiftActions.LiftADT.*;

@TeleOp(name="Manager Control Test", group="Pushbot")
public class ManagerTeleOp extends LinearOpMode {
    private int ticker = 0;

    BotConfig robot = new BotConfig();

    Manager<ToMethod> manager = Manager.Builder.builder()
            .addFunc(RAISELIFT)
            .addFunc(LOWERLIFT)
            .addFunc(CONTROLLER_CHECK)
            .addFunc(DRIVE)
            .addFunc(DROP)
            .addFunc(SPIN_ABDUCTOR)
            .addFunc(TURBO)
            .addFunc(Manage_Auto_Lift_Return())
            .addParameterUnsafe(robot)
            .setThreads(10)
            .build();

    //FIXME: errors are not thrown properly, NullPointerExceptions are thrown instead
    //FIXME: when manual args are not supplied, ArrayOutOfBoundsExceptions are thrown instead
    //TODO: execIf with packaged datatype for a boolean AND arguments to pass when successful,
    //      such as for the abductor
    public void runOpMode() {
        robot.init(hardwareMap);

        waitForStart();
        while(opModeIsActive()) {
            telemetry.addData("gamepad1 a ", gamepad1.a); //this is debug info
            telemetry.addData("gamepad1 b ", gamepad1.b);
            telemetry.addData("ticker ", ticker);
            telemetry.addData("active threads: ", Thread.activeCount());
            telemetry.update();
            ticker++;

            //TODO: Add scheduled execution, then implement auto lift and auto return
            manager .exec(DRIVE, gamepad1)
                    .execIf(CheckItemDetectedL(robot), RAISELIFT)
                    //LIFT & BASKET
                    .execIf(CheckControllerL(gamepad1, "a"), RAISELIFT)
                    .execIf(CheckControllerL(gamepad1, "b"), LOWERLIFT)
                    .execIf(CheckControllerL(gamepad1, "x"), DROP)
                    //TURBO
                    .execIf(CheckControllerL(gamepad1, "x"), TURBO, mkArr("fw"))
                    .execIf(CheckControllerL(gamepad1, "y"), TURBO, mkArr("bw"))
                    //ABDUCTOR
                    .execIf(CheckControllerL(gamepad1, "lb"), SPIN_ABDUCTOR, mkArr("cw"))
                    .execIf(CheckControllerL(gamepad1, "rb"), SPIN_ABDUCTOR, mkArr("ccw"))
                    .execIf(CheckControllerL(gamepad1, "bump_not_held"), SPIN_ABDUCTOR, mkArr("halt"));
//                    .await();

        }
    }

    @SafeVarargs //check for heap pollution (how?)
    private final <T> T[] mkArr(T... literals) {
        return literals;
    }
}
